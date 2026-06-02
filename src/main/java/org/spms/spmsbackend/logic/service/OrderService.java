package org.spms.spmsbackend.logic.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spms.spmsbackend.domain.notifications.Notification;
import org.spms.spmsbackend.domain.orders.Order;
import org.spms.spmsbackend.domain.orders.OrderSize;
import org.spms.spmsbackend.domain.orders.OrderStatus;
import org.spms.spmsbackend.entity.PackageEntity;
import org.spms.spmsbackend.entity.ShelfRowEntity;
import org.spms.spmsbackend.entity.UserEntity;
import org.spms.spmsbackend.exceptions.NotFoundException;
import org.spms.spmsbackend.logic.interfaces.IOrderService;
import org.spms.spmsbackend.mapper.PackageMapper;
import org.spms.spmsbackend.repository.PackageRepository;
import org.spms.spmsbackend.repository.ShelfRowRepository;
import org.spms.spmsbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService implements IOrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final NotificationService notificationService;
    private final PackageRepository packageRepository;
    private final PackageMapper packageMapper;
    private final UserRepository userRepository;
    private final ShelfRowRepository shelfRowRepository;

    public OrderService(
            NotificationService notificationService,
            PackageRepository packageRepository,
            PackageMapper packageMapper,
            UserRepository userRepository,
            ShelfRowRepository shelfRowRepository
    ) {
        this.notificationService = notificationService;
        this.packageRepository = packageRepository;
        this.packageMapper = packageMapper;
        this.userRepository = userRepository;
        this.shelfRowRepository = shelfRowRepository;
    }

    @Override
    public List<Order> getAllOrders() {
        return packageRepository.findAll()
                .stream()
                .map(packageMapper::toDomain)
                .toList();
    }

    @Override
    public Order getOrderById(int id) {
        return packageRepository.findById(id)
                .map(packageMapper::toDomain)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Order getOrderByTrackingNumber(String trackingNumber) {
        return packageRepository.findByTrackingNumber(trackingNumber)
                .map(packageMapper::toDomain)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public List<Order> getOrdersForEmployee(int employeeId) {
        return packageRepository.findByEmployee_Id(employeeId)
                .stream()
                .map(packageMapper::toDomain)
                .toList();
    }

    @Override
    public Order createOrder(int employeeId, String trackingNumber, OrderSize size) {
        throw new UnsupportedOperationException("Not implemented in current sprint");
    }

    @Override
    public Order storePackage(String trackingNumber, Integer employeeId, Integer rowId, String recipientName) {
        PackageEntity entity = packageRepository.findByTrackingNumber(trackingNumber)
                .orElse(new PackageEntity());

        entity.setTrackingNumber(trackingNumber);
        entity.setRecipientName(recipientName);

        // employeeId is null for Sioux / unknown packages.
        if (employeeId != null) {
            UserEntity employee = userRepository.findById(employeeId)
                    .orElseThrow(NotFoundException::new);
            entity.setEmployee(employee);
        } else {
            entity.setEmployee(null);
        }

        ShelfRowEntity row = shelfRowRepository.findById(rowId)
                .orElseThrow(NotFoundException::new);
        entity.setAssignedRow(row);
        entity.setAssignedAt(LocalDateTime.now());
        entity.setStatus("STORED");
        entity.setPickedUp(false);

        PackageEntity saved = packageRepository.save(entity);

        // For unknown/unaddressed packages (no employee, no recipient name) skip
        // the auto-notification — the receptionist will broadcast a custom message
        // via broadcastUnknownPackage so employees only get one notification.
        // For Sioux packages: classifier → COMPANY → fans out to managers.
        // For personal packages: classifier → EMPLOYEE → notifies the owner.
        boolean isUnknown = employeeId == null
                && (recipientName == null || recipientName.isBlank());
        if (!isUnknown) {
            try {
                notificationService.notifyPackageArrived(saved, null);
                packageRepository.save(saved);
            } catch (Exception ex) {
                log.warn("notifyPackageArrived failed for package {}: {}",
                        saved.getId(), ex.getMessage());
            }
        }

        return packageMapper.toDomain(saved);
    }

    @Override
    public Notification broadcastUnknownPackage(int orderId, String customMessage) {
        PackageEntity entity = packageRepository.findById(orderId)
                .orElseThrow(NotFoundException::new);
        return notificationService.notifyUnknownBroadcast(entity, customMessage);
    }

    @Override
    public Order markAsPickedUp(int orderId) {
        PackageEntity entity = packageRepository.findById(orderId)
                .orElseThrow(NotFoundException::new);

        entity.setPickedUp(true);
        entity.setPickedUpAt(LocalDateTime.now());
        entity.setStatus("PICKED_UP");

        PackageEntity saved = packageRepository.save(entity);

        // US-03: send a pickup confirmation to the owner so they get a
        // receipt in-app + via email. Failures are logged so the actual
        // pickup operation never gets blocked by mail/notification issues.
        try {
            notificationService.notifyPickupConfirmed(saved);
        } catch (Exception ex) {
            log.warn("notifyPickupConfirmed failed for package {}: {}",
                    saved.getId(), ex.getMessage());
        }

        return packageMapper.toDomain(saved);
    }

    @Override
    public Order updateStatus(int orderId, OrderStatus newStatus) {
        PackageEntity entity = packageRepository.findById(orderId)
                .orElseThrow(NotFoundException::new);

        entity.setStatus(newStatus.getName());
        PackageEntity saved = packageRepository.save(entity);
        return packageMapper.toDomain(saved);
    }

    @Override
    public void deleteOrder(int id) {
        packageRepository.deleteById(id);
    }

    @Override
    public Notification createArrivalNotification(int orderId, List<Integer> targetUserIds) {
        PackageEntity entity = packageRepository.findById(orderId)
                .orElseThrow(NotFoundException::new);
        Notification result = notificationService.notifyPackageArrived(entity, targetUserIds);
        // Persist any classification update the service made.
        packageRepository.save(entity);
        return result;
    }

    @Override
    public Notification createPickupReminderNotification(int orderId) {
        PackageEntity entity = packageRepository.findById(orderId)
                .orElseThrow(NotFoundException::new);
        return notificationService.notifyPickupReminder(entity);
    }
}