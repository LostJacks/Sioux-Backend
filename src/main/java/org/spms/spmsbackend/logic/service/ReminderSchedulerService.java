package org.spms.spmsbackend.logic.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spms.spmsbackend.entity.PackageEntity;
import org.spms.spmsbackend.entity.ReminderSettingsEntity;
import org.spms.spmsbackend.repository.PackageRepository;
import org.spms.spmsbackend.repository.ReminderSettingsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Automatically sends pickup-reminder notifications for packages that
 * have not been collected yet.
 *
 * <p>The scheduler runs every hour. For each non-picked-up, employee-owned
 * package it checks:
 * <ol>
 *   <li>Is the global reminder feature enabled?</li>
 *   <li>Has the initial delay elapsed since the package was assigned?</li>
 *   <li>If subsequent reminders are configured, has the repeat interval
 *       elapsed since the last reminder?</li>
 *   <li>Has the maximum reminder count been reached?</li>
 * </ol>
 * When all conditions pass it calls {@link NotificationService#notifyPickupReminder}
 * (which persists a notification row and sends an email) and increments the
 * package's reminder counter.
 * </p>
 */
@Service
public class ReminderSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(ReminderSchedulerService.class);

    private final PackageRepository packageRepository;
    private final ReminderSettingsRepository settingsRepository;
    private final NotificationService notificationService;

    public ReminderSchedulerService(PackageRepository packageRepository,
                                    ReminderSettingsRepository settingsRepository,
                                    NotificationService notificationService) {
        this.packageRepository = packageRepository;
        this.settingsRepository = settingsRepository;
        this.notificationService = notificationService;
    }

    /**
     * Runs every hour on the hour. The cron can be changed in
     * {@code application.properties} via {@code spms.reminder.cron}.
     */
    @Scheduled(cron = "${spms.reminder.cron:0 0 * * * *}")
    @Transactional
    public void sendScheduledReminders() {
        ReminderSettingsEntity settings = settingsRepository.findById(1).orElse(null);

        if (settings == null || !Boolean.TRUE.equals(settings.getEnabled())) {
            log.debug("[ReminderScheduler] reminders disabled — skipping run");
            return;
        }

        int initialDelayHours  = settings.getInitialDelayHours()  != null ? settings.getInitialDelayHours()  : 24;
        int repeatIntervalHours = settings.getRepeatIntervalHours() != null ? settings.getRepeatIntervalHours() : 24;
        int maxReminders        = settings.getMaxReminders()        != null ? settings.getMaxReminders()        : 3;

        List<PackageEntity> pending = packageRepository.findByPickedUpFalseAndEmployeeIsNotNull();
        log.info("[ReminderScheduler] checking {} pending package(s)", pending.size());

        LocalDateTime now = LocalDateTime.now();
        int sent = 0;

        for (PackageEntity pkg : pending) {
            // Use assignedAt as the reference; fall back to now (shouldn't happen).
            LocalDateTime assignedAt = pkg.getAssignedAt() != null ? pkg.getAssignedAt() : now;
            int count = pkg.getReminderCount();

            // Hard cap check
            if (maxReminders > 0 && count >= maxReminders) {
                continue;
            }

            boolean shouldSend;
            if (count == 0) {
                // First reminder: must wait initialDelayHours after arrival
                shouldSend = now.isAfter(assignedAt.plusHours(initialDelayHours));
            } else {
                // Subsequent reminders: repeat interval must have passed
                if (repeatIntervalHours <= 0) {
                    // No repeats configured
                    continue;
                }
                LocalDateTime lastSent = pkg.getLastReminderSentAt();
                shouldSend = lastSent != null
                        && now.isAfter(lastSent.plusHours(repeatIntervalHours));
            }

            if (!shouldSend) continue;

            try {
                notificationService.notifyPickupReminder(pkg);
                pkg.setReminderCount(count + 1);
                pkg.setLastReminderSentAt(now);
                packageRepository.save(pkg);
                sent++;
                log.info("[ReminderScheduler] reminder #{} sent for package {} (employee {})",
                        count + 1, pkg.getId(), pkg.getEmployee().getId());
            } catch (Exception e) {
                log.error("[ReminderScheduler] failed to send reminder for package {}: {}",
                        pkg.getId(), e.getMessage());
            }
        }

        log.info("[ReminderScheduler] run complete — {} reminder(s) sent", sent);
    }
}
