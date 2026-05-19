package org.spms.spmsbackend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Global reminder configuration (single-row table).
 *
 * <p>Only one row is expected (id = 1). The scheduler reads this row
 * before each run to decide whether automatic reminders are enabled and
 * what the delay / interval / cap should be.</p>
 */
@Entity
@Table(name = "ReminderSettings", schema = "dbo")
public class ReminderSettingsEntity {

    @Id
    @Column(name = "id")
    private Integer id = 1;

    /** Hours after a package arrives before the first reminder is sent. */
    @Column(name = "initial_delay_hours", nullable = false)
    private Integer initialDelayHours = 24;

    /**
     * Hours between subsequent reminders.
     * Set to 0 to send only the initial reminder (no repeats).
     */
    @Column(name = "repeat_interval_hours", nullable = false)
    private Integer repeatIntervalHours = 24;

    /** Maximum number of reminders per package (0 = unlimited). */
    @Column(name = "max_reminders", nullable = false)
    private Integer maxReminders = 3;

    /** When false the scheduler skips all processing. */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    void onSave() {
        updatedAt = LocalDateTime.now();
    }

    // ---- Getters / Setters ----

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getInitialDelayHours() { return initialDelayHours; }
    public void setInitialDelayHours(Integer initialDelayHours) { this.initialDelayHours = initialDelayHours; }

    public Integer getRepeatIntervalHours() { return repeatIntervalHours; }
    public void setRepeatIntervalHours(Integer repeatIntervalHours) { this.repeatIntervalHours = repeatIntervalHours; }

    public Integer getMaxReminders() { return maxReminders; }
    public void setMaxReminders(Integer maxReminders) { this.maxReminders = maxReminders; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
