package org.spms.spmsbackend.logic.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spms.spmsbackend.domain.notifications.NotificationType;
import org.spms.spmsbackend.entity.PackageEntity;
import org.spms.spmsbackend.repository.NotificationRepository;
import org.spms.spmsbackend.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled job that automatically sends pickup reminder notifications to
 * employees whose packages have been waiting longer than the configured
 * threshold without being picked up.
 *
 * <p>Runs on a configurable cron expression (default: every day at 09:00).
 * A de-duplication guard prevents sending more than one reminder per package
 * per {@code spms.reminder.dedup-hours} window (default 24 h).</p>
 *
 * <p>Configuration properties:</p>
 * <ul>
 *   <li>{@code spms.reminder.threshold-days} — how many days a package must
 *       have been waiting before a reminder is sent (default: 1)</li>
 *   <li>{@code spms.reminder.cron} — Spring cron expression for the job
 *       (default: {@code 0 0 9 * * *} = every day at 09:00)</li>
 *   <li>{@code spms.reminder.dedup-hours} — minimum hours between two
 *       reminders for the same package (default: 24)</li>
 * </ul>
 */
@Service
public class ReminderSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(ReminderSchedulerService.class);

    private final PackageRepository packageRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    private final int thresholdDays;
    private final int dedupHours;

    public ReminderSchedulerService(
            PackageRepository packageRepository,
            NotificationRepository notificationRepository,
            NotificationService notificationService,
            @Value("${spms.reminder.threshold-days:1}") int thresholdDays,
            @Value("${spms.reminder.dedup-hours:24}") int dedupHours) {
        this.packageRepository = packageRepository;
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
        this.thresholdDays = thresholdDays;
        this.dedupHours = dedupHours;
    }

    /**
     * Main scheduled entry point. Finds all undelivered packages older than
     * {@code thresholdDays}, skips those that already received a reminder
     * within the de-dup window, and fires a reminder for the rest.
     */
    @Scheduled(cron = "${spms.reminder.cron:0 0 9 * * *}")
    public void sendPendingPickupReminders() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(thresholdDays);
        List<PackageEntity> overdue = packageRepository.findByPickedUpFalseAndAssignedAtBefore(threshold);

        if (overdue.isEmpty()) {
            log.info("[ReminderScheduler] No overdue packages found — nothing to do.");
            return;
        }

        log.info("[ReminderScheduler] Found {} overdue package(s); processing reminders.", overdue.size());
        int sent = 0;
        int skipped = 0;

        for (PackageEntity pkg : overdue) {
            if (pkg.getEmployee() == null) {
                log.warn("[ReminderScheduler] Package {} has no assigned employee — skipping.", pkg.getId());
                skipped++;
                continue;
            }

            // De-duplication: skip if a reminder was already sent recently.
            LocalDateTime dedupCutoff = LocalDateTime.now().minusHours(dedupHours);
            boolean alreadySent = notificationRepository.existsByRelatedPackage_IdAndTypeAndCreatedAtAfter(
                    pkg.getId(),
                    NotificationType.PICKUP_REMINDER.name(),
                    dedupCutoff
            );

            if (alreadySent) {
                log.debug("[ReminderScheduler] Package {} already reminded within {}h — skipping.",
                        pkg.getId(), dedupHours);
                skipped++;
                continue;
            }

            try {
                notificationService.notifyPickupReminder(pkg);
                log.info("[ReminderScheduler] Reminder sent for package {} (employee {}).",
                        pkg.getId(), pkg.getEmployee().getId());
                sent++;
            } catch (Exception e) {
                log.error("[ReminderScheduler] Failed to send reminder for package {}: {}",
                        pkg.getId(), e.getMessage(), e);
            }
        }

        log.info("[ReminderScheduler] Done. Sent: {}, Skipped: {}.", sent, skipped);
    }
}
