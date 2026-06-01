package org.spms.spmsbackend.logic.service;

import org.spms.spmsbackend.entity.ReminderSettingsEntity;
import org.spms.spmsbackend.repository.ReminderSettingsRepository;
import org.springframework.stereotype.Service;

/**
 * Manages the singleton ReminderSettings row.
 * Creates default settings on first access if none exist.
 */
@Service
public class ReminderSettingsService {

    private static final int SINGLETON_ID = 1;

    private final ReminderSettingsRepository repository;

    public ReminderSettingsService(ReminderSettingsRepository repository) {
        this.repository = repository;
    }

    public ReminderSettingsEntity getSettings() {
        return repository.findById(SINGLETON_ID).orElseGet(this::createDefaults);
    }

    public ReminderSettingsEntity updateSettings(ReminderSettingsEntity incoming) {
        incoming.setId(SINGLETON_ID);
        return repository.save(incoming);
    }

    private ReminderSettingsEntity createDefaults() {
        ReminderSettingsEntity defaults = new ReminderSettingsEntity();
        defaults.setId(SINGLETON_ID);
        return repository.save(defaults);
    }
}
