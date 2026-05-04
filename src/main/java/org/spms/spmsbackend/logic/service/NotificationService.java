package org.spms.spmsbackend.logic.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spms.spmsbackend.domain.notifications.Notification;
import org.spms.spmsbackend.domain.notifications.NotificationChannel;
import org.spms.spmsbackend.domain.notifications.NotificationScope;
import org.spms.spmsbackend.domain.notifications.NotificationType;
import org.spms.spmsbackend.domain.orders.AddressedTo;
import org.spms.spmsbackend.domain.orders.Order;
import org.spms.spmsbackend.entity.NotificationEntity;
import org.spms.spmsbackend.entity.PackageEntity;
import org.spms.spmsbackend.entity.UserEntity;
import org.spms.spmsbackend.exceptions.NotFoundException;
import org.spms.spmsbackend.mapper.NotificationMapper;
import org.spms.spmsbackend.repository.NotificationRepository;
import org.spms.spmsbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Builds, persists, and dispatches notifications.
 *
 * <p>Pre-existing methods {@link #createOrderArrivedNotification(Order)} and
 * {@link #createPickupReminderNotification(Order)} still work and still
 * return non-persistent POJOs — callers that already use them stay green.
 * The new {@code notifyPackageArrived} family persists rows in
 * {@code Notifications} and triggers email through {@link MailService}.</p>
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final SiouxPackageClassifier classifier;
    private final List<String> fallbackRecipients;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationMapper notificationMapper,
                               UserRepository userRepository,
                               MailService mailService,
                               SiouxPackageClassifier classifier,
                               @Value("${spms.mail.fallback-recipients:}") String fallbackCsv) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.classifier = classifier;
        this.fallbackRecipients = parseCsv(fallbackCsv);
    }

    // ----- Legacy POJO factories (kept for backward compatibility) -----

    public Notification createOrderArrivedNotification(Order order) {
        Notification notification = new Notification();
        notification.setUserId(safeEmployeeId(order));
        notification.setTitle("Your package has arrived");
        notification.setMessage(buildArrivalMessage(order));
        notification.setType(NotificationType.ORDER_ARRIVED);
        notification.setScope(NotificationScope.PERSONAL);
        notification.setChannel(NotificationChannel.IN_APP);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        return notification;
    }

    public Notification createPickupReminderNotification(Order order) {
        Notification notification = new Notification();
        notification.setUserId(safeEmployeeId(order));
        notification.setTitle("Pickup reminder");
        notification.setMessage(buildReminderMessage(order));
        notification.setType(NotificationType.PICKUP_REMINDER);
        notification.setScope(NotificationScope.PERSONAL);
        notification.setChannel(NotificationChannel.IN_APP);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        return notification;
    }

    // ----- New, persisting + mailing flow (US-03) -----

    /**
     * Notify the right people that a package has arrived. The classifier
     * decides whether this is personal (employee package) or company-wide
     * (Sioux / unaddressed). For company-wide events, the receptionist may
     * pass {@code targetUserIds} to override the default fan-out to all
     * managers.
     *
     * @return a representative Notification (the first one persisted) so
     *         existing callers that expect a single body stay compatible.
     */
    public Notification notifyPackageArrived(PackageEntity pkg, List<Integer> targetUserIds) {
        if (pkg == null) {
            throw new NotFoundException();
        }

        AddressedTo addressedTo = classifier.classify(pkg);
        // Persist the classification so it travels with the package row.
        if (pkg.getAddressedTo() == null || pkg.getAddressedTo().isBlank()) {
            pkg.setAddressedTo(addressedTo.name());
        }

        if (addressedTo == AddressedTo.EMPLOYEE) {
            return notifyEmployeeArrival(pkg);
        }
        return notifyCompanyArrival(pkg, addressedTo, targetUserIds);
    }

    /** Personal arrival notification for the package's owner. */
    private Notification notifyEmployeeArrival(PackageEntity pkg) {
        UserEntity owner = pkg.getEmployee();
        NotificationEntity entity = persist(
                owner,
                pkg,
                NotificationScope.PERSONAL,
                NotificationType.ORDER_ARRIVED,
                NotificationChannel.BOTH,
                "Your package has arrived",
                buildArrivalMessageForEntity(pkg)
        );

        if (owner != null && owner.getEmail() != null && !owner.getEmail().isBlank()) {
            mailService.send(List.of(owner.getEmail()),
                    entity.getTitle(),
                    entity.getMessage());
        } else {
            log.warn("[NotificationService] employee {} has no email; in-app only",
                    owner == null ? "?" : owner.getId());
        }

        return notificationMapper.toDomain(entity);
    }

    /**
     * Company / unaddressed arrival — fan out to managers (or to the
     * receptionist-selected subset).
     */
    private Notification notifyCompanyArrival(PackageEntity pkg,
                                              AddressedTo addressedTo,
                                              List<Integer> targetUserIds) {
        List<UserEntity> targets = resolveTargets(targetUserIds);

        NotificationType type = addressedTo == AddressedTo.UNKNOWN
                ? NotificationType.UNADDRESSED_PACKAGE
                : NotificationType.COMPANY_PACKAGE;

        String title = addressedTo == AddressedTo.UNKNOWN
                ? "Unaddressed package received"
                : "Business package addressed to " + classifier.getCompanyName();

        String body = buildCompanyMessage(pkg, addressedTo);

        if (targets.isEmpty()) {
            // Last-ditch: at least mail the configured fallback list (if any)
            // and persist a stub against a synthetic recipient — but without
            // a real user we can't satisfy NOT NULL recipient_user_id. Log
            // and fall back to mail-only.
            log.warn("[NotificationService] no manager targets for package {} ({}); mail-only",
                    pkg.getId(), addressedTo);
            if (!fallbackRecipients.isEmpty()) {
                mailService.send(fallbackRecipients, title, body);
            }
            // Build a non-persistent stub so the API caller still gets a body.
            return synthetic(pkg, type, title, body);
        }

        List<NotificationEntity> persisted = new ArrayList<>();
        List<String> mailRecipients = new ArrayList<>(fallbackRecipients);
        for (UserEntity target : targets) {
            NotificationEntity row = persist(
                    target,
                    pkg,
                    NotificationScope.GLOBAL,
                    type,
                    NotificationChannel.BOTH,
                    title,
                    body
            );
            persisted.add(row);
            if (target.getEmail() != null && !target.getEmail().isBlank()) {
                mailRecipients.add(target.getEmail());
            }
        }
        mailService.send(distinct(mailRecipients), title, body);

        return notificationMapper.toDomain(persisted.get(0));
    }

    /** Pickup reminder, persisted + mailed to the owner. */
    public Notification notifyPickupReminder(PackageEntity pkg) {
        if (pkg == null || pkg.getEmployee() == null) {
            throw new NotFoundException();
        }
        UserEntity owner = pkg.getEmployee();
        String title = "Pickup reminder";
        String message = buildReminderMessageForEntity(pkg);

        NotificationEntity entity = persist(
                owner,
                pkg,
                NotificationScope.PERSONAL,
                NotificationType.PICKUP_REMINDER,
                NotificationChannel.BOTH,
                title,
                message
        );

        if (owner.getEmail() != null && !owner.getEmail().isBlank()) {
            mailService.send(List.of(owner.getEmail()), title, message);
        }
        return notificationMapper.toDomain(entity);
    }

    // ----- Read APIs -----

    public List<Notification> getForEmployee(int employeeId) {
        return notificationRepository
                .findByRecipient_IdOrderByCreatedAtDesc(employeeId)
                .stream()
                .map(notificationMapper::toDomain)
                .toList();
    }

    public List<Notification> getGlobalForManager(int managerId) {
        return notificationRepository
                .findByRecipient_IdAndScopeOrderByCreatedAtDesc(managerId, NotificationScope.GLOBAL.name())
                .stream()
                .map(notificationMapper::toDomain)
                .toList();
    }

    public List<Notification> getNewerThan(int employeeId, int afterId) {
        return notificationRepository
                .findByRecipient_IdAndIdGreaterThanOrderByCreatedAtDesc(employeeId, afterId)
                .stream()
                .map(notificationMapper::toDomain)
                .toList();
    }

    public Notification markAsRead(int notificationId) {
        NotificationEntity entity = notificationRepository.findById(notificationId)
                .orElseThrow(NotFoundException::new);
        if (!Boolean.TRUE.equals(entity.getRead())) {
            entity.setRead(true);
            entity.setReadAt(LocalDateTime.now());
            entity = notificationRepository.save(entity);
        }
        return notificationMapper.toDomain(entity);
    }

    // ----- Helpers -----

    private List<UserEntity> resolveTargets(List<Integer> targetUserIds) {
        if (targetUserIds != null && !targetUserIds.isEmpty()) {
            // Receptionist explicitly picked managers.
            List<UserEntity> picked = new ArrayList<>();
            for (Integer id : targetUserIds) {
                if (id == null) continue;
                userRepository.findById(id).ifPresent(picked::add);
            }
            return picked;
        }
        // Default fan-out: every manager in the system.
        return userRepository.findByRoleIgnoreCase("MANAGER");
    }

    private NotificationEntity persist(UserEntity recipient,
                                       PackageEntity pkg,
                                       NotificationScope scope,
                                       NotificationType type,
                                       NotificationChannel channel,
                                       String title,
                                       String message) {
        NotificationEntity entity = new NotificationEntity();
        entity.setRecipient(recipient);
        entity.setRelatedPackage(pkg);
        entity.setScope(scope.name());
        entity.setType(type.name());
        entity.setChannel(channel.name());
        entity.setTitle(title);
        entity.setMessage(message);
        entity.setRead(false);
        return notificationRepository.save(entity);
    }

    private Notification synthetic(PackageEntity pkg,
                                   NotificationType type,
                                   String title,
                                   String body) {
        Notification n = new Notification();
        n.setUserId(0);
        n.setPackageId(pkg.getId());
        n.setTitle(title);
        n.setMessage(body);
        n.setType(type);
        n.setScope(NotificationScope.GLOBAL);
        n.setChannel(NotificationChannel.EMAIL);
        n.setRead(false);
        n.setCreatedAt(LocalDateTime.now());
        return n;
    }

    private String buildArrivalMessage(Order order) {
        String building = order != null && order.getBuildingName() != null
                ? order.getBuildingName() : "the front desk";
        String deadline = order != null && order.getPickupDeadline() != null
                ? order.getPickupDeadline().format(FORMATTER) : "soon";
        return "Your package has arrived at " + building
                + ". Please collect it before " + deadline + ".";
    }

    private String buildReminderMessage(Order order) {
        String building = order != null && order.getBuildingName() != null
                ? order.getBuildingName() : "the front desk";
        String deadline = order != null && order.getPickupDeadline() != null
                ? order.getPickupDeadline().format(FORMATTER) : "soon";
        return "Your package at " + building
                + " must be collected within 2 days. Final pickup date: " + deadline + ".";
    }

    private String buildArrivalMessageForEntity(PackageEntity pkg) {
        String building = pkg.getEmployee() != null && pkg.getEmployee().getBuilding() != null
                ? pkg.getEmployee().getBuilding().getName() : "the front desk";
        String tracking = pkg.getTrackingNumber() != null ? pkg.getTrackingNumber() : "(no tracking)";
        return "Your package " + tracking + " has arrived at " + building
                + ". Please collect it as soon as possible.";
    }

    private String buildReminderMessageForEntity(PackageEntity pkg) {
        String building = pkg.getEmployee() != null && pkg.getEmployee().getBuilding() != null
                ? pkg.getEmployee().getBuilding().getName() : "the front desk";
        String tracking = pkg.getTrackingNumber() != null ? pkg.getTrackingNumber() : "(no tracking)";
        return "Reminder: package " + tracking + " is still waiting at " + building
                + ". Please pick it up within 2 days.";
    }

    private String buildCompanyMessage(PackageEntity pkg, AddressedTo addressedTo) {
        String tracking = pkg.getTrackingNumber() != null ? pkg.getTrackingNumber() : "(no tracking)";
        String recipient = pkg.getRecipientName() != null && !pkg.getRecipientName().isBlank()
                ? pkg.getRecipientName() : "(no recipient on parcel)";
        if (addressedTo == AddressedTo.UNKNOWN) {
            return "An unaddressed package (" + tracking + ", listed recipient: "
                    + recipient + ") arrived at the front desk. Please assign it.";
        }
        return "A business package (" + tracking + ", addressed to "
                + recipient + ") arrived for " + classifier.getCompanyName()
                + ". Please coordinate pickup so the business cycle is not interrupted.";
    }

    private static int safeEmployeeId(Order order) {
        if (order == null || order.getEmployee() == null) return 0;
        return order.getEmployee().getId();
    }

    private static List<String> parseCsv(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        List<String> out = new ArrayList<>();
        for (String s : csv.split(",")) {
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) out.add(trimmed);
        }
        return out;
    }

    private static List<String> distinct(List<String> in) {
        List<String> out = new ArrayList<>();
        for (String s : in) {
            if (s != null && !s.isBlank() && !out.contains(s)) {
                out.add(s);
            }
        }
        return Collections.unmodifiableList(out);
    }

    @SuppressWarnings("unused")
    private Optional<UserEntity> findUser(Integer id) {
        return id == null ? Optional.empty() : userRepository.findById(id);
    }
}
