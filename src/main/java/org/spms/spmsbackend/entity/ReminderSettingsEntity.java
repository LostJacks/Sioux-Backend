package org.spms.spmsbackend.entity;

import jakarta.persistence.*;

/**
 * Singleton row that stores the receptionist-configurable reminder settings.
 * There is always exactly one row (id = 1); the service seeds it with
 * defaults on first access.
 */
@Entity
@Table(name = "ReminderSettings", schema = "dbo")
public class ReminderSettingsEntity {

    @Id
    @Column(name = "id")
    private Integer id = 1;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    /** Hours after a package arrives before the first reminder is sent. */
    @Column(name = "initial_delay_hours", nullable = false)
    private int initialDelayHours = 24;

    /** Hours between follow-up reminders. 0 = no repeat. */
    @Column(name = "repeat_interval_hours", nullable = false)
    private int repeatIntervalHours = 24;

    /** Max reminders per package. 0 = unlimited. */
    @Column(name = "max_reminders", nullable = false)
    private int maxReminders = 3;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public int getInitialDelayHours() { return initialDelayHours; }
    public void setInitialDelayHours(int initialDelayHours) { this.initialDelayHours = initialDelayHours; }

    public int getRepeatIntervalHours() { return repeatIntervalHours; }
    public void setRepeatIntervalHours(int repeatIntervalHours) { this.repeatIntervalHours = repeatIntervalHours; }

    public int getMaxReminders() { return maxReminders; }
    public void setMaxReminders(int maxReminders) { this.maxReminders = maxReminders; }
}
