package org.spms.spmsbackend.dtos;

/**
 * Request / response body for the reminder-settings endpoint.
 */
public class ReminderSettingsDTO {

    /** Hours after package arrival before the first reminder is sent. */
    private Integer initialDelayHours;

    /**
     * Hours between subsequent reminders.
     * 0 means "no repeat — only send the initial reminder".
     */
    private Integer repeatIntervalHours;

    /** Maximum number of reminders per package. 0 = unlimited. */
    private Integer maxReminders;

    /** Whether the automatic reminder scheduler is active. */
    private Boolean enabled;

    public Integer getInitialDelayHours() { return initialDelayHours; }
    public void setInitialDelayHours(Integer initialDelayHours) { this.initialDelayHours = initialDelayHours; }

    public Integer getRepeatIntervalHours() { return repeatIntervalHours; }
    public void setRepeatIntervalHours(Integer repeatIntervalHours) { this.repeatIntervalHours = repeatIntervalHours; }

    public Integer getMaxReminders() { return maxReminders; }
    public void setMaxReminders(Integer maxReminders) { this.maxReminders = maxReminders; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
