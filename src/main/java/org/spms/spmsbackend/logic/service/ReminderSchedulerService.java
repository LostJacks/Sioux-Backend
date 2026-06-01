package org.spms.spmsbackend.logic.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spms.spmsbackend.domain.notifications.NotificationType;
import org.spms.spmsbackend.entity.PackageEntity;
import org.spms.spmsbackend.entity.ReminderSettingsEntity;
import org.spms.spmsbackend.repository.NotificationRepository;
import org.spms.spmsbackend.repository.PackageRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled job that automatically sends pickup reminder notifications.
 * All settings (enabled, delays, limits) are read from the DB at runtime
 * via {@link ReminderSettingsService}, so the receptionist's UI changes
 * take effect on the next scheduler run without a restart.
 *
 * <p>Runs every minute; the settings control whether anything actually happens.</p>
 */
@Service
public class ReminderSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(ReminderSchedulerService.class);
    private static final String REMINDER_TYPE = NotificationType.PICKUP_REMINDER.name();

    private final PackageRepository packageRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final ReminderSettingsService reminderSettingsService;

    public ReminderSchedulerService(PackageRepository packageRepository,
                                    NotificationRepository notificationRepository,
                                    NotificationService notificationService,
                                    ReminderSettingsService reminderSettingsService) {
        this.packageRepository = packageRepository;
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
        this.reminderSettingsService = reminderSettingsService;
    }

    @Scheduled(cron = "${spms.reminder.cron:0 * * * * *}")
    public void sendPendingPickupReminders() {
        ReminderSettingsEntity settings = reminderSettingsService.getSettings();

        if (!settings.isEnabled()) {
            log.debug("[ReminderScheduler] Reminders are disabled — skipping.");
            return;
        }

        LocalDateTime threshold = LocalDateTime.now().minusHours(settings.getInitialDelayHours());
        List<PackageEntity> overdue = packageRepository.findByPickedUpFalseAndAssignedAtBefore(threshold);

        if (overdue.isEmpty()) {
            log.info("[ReminderScheduler] No overdue packages — nothing to do.");
            return;
        }

        log.info("[ReminderScheduler] Found {} overdue package(s).", overdue.size());
        int sent = 0;
        int skipped = 0;

        for (PackageEntity pkg : overdue) {
            if (pkg.getEmployee() == null) {
                log.warn("[ReminderScheduler] Package {} has no employee — skipping.", pkg.getId());
                skipped++;
                continue;
            }

            // Check maxReminders limit (0 = unlimited)
            if (settings.getMaxReminders() > 0) {
                int alreadySent = notificationRepository.countByRelatedPackage_IdAndType(pkg.getId(), REMINDER_TYPE);
                if (alreadySent >= settings.getMaxReminders()) {
                    log.debug("[ReminderScheduler] Package {} reached max reminders ({}) — skipping.",
                            pkg.getId(), settings.getMaxReminders());
                    skipped++;
                    continue;
                }
            }

            // De-duplication: skip if a reminder was sent within the repeat interval
            int dedupHours = settings.getRepeatIntervalHours() > 0
                    ? settings.getRepeatIntervalHours()
                    : settings.getInitialDelayHours();
            LocalDateTime dedupCutoff = LocalDateTime.now().minusHours(dedupHours);
            boolean alreadySentRecently = notificationRepository
                    .existsByRelatedPackage_IdAndTypeAndCreatedAtAfter(pkg.getId(), REMINDER_TYPE, dedupCutoff);

            if (alreadySentRecently) {
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
                log.error("[ReminderScheduler] Failed for package {}: {}", pkg.getId(), e.getMessage(), e);
            }
        }

        log.info("[ReminderScheduler] Done. Sent: {}, Skipped: {}.", sent, skipped);
    }
}
