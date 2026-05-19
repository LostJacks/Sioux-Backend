package org.spms.spmsbackend.controllers;

import org.spms.spmsbackend.dtos.ReminderSettingsDTO;
import org.spms.spmsbackend.entity.ReminderSettingsEntity;
import org.spms.spmsbackend.repository.ReminderSettingsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for managing automatic reminder settings.
 *
 * <p>GET  /api/reminder-settings        → read current configuration
 * <p>PUT  /api/reminder-settings        → update configuration
 * <p>POST /api/reminder-settings/trigger → manually trigger the scheduler once (debug / receptionist use)
 */
@RestController
@RequestMapping("/api/reminder-settings")
@CrossOrigin(origins = "http://localhost:5173")
public class ReminderSettingsController {

    private final ReminderSettingsRepository repository;

    public ReminderSettingsController(ReminderSettingsRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<ReminderSettingsDTO> get() {
        ReminderSettingsEntity entity = repository.findById(1)
                .orElseGet(this::defaultSettings);
        return ResponseEntity.ok(toDto(entity));
    }

    @PutMapping
    public ResponseEntity<ReminderSettingsDTO> update(@RequestBody ReminderSettingsDTO dto) {
        ReminderSettingsEntity entity = repository.findById(1)
                .orElseGet(this::defaultSettings);

        if (dto.getInitialDelayHours() != null)  entity.setInitialDelayHours(dto.getInitialDelayHours());
        if (dto.getRepeatIntervalHours() != null) entity.setRepeatIntervalHours(dto.getRepeatIntervalHours());
        if (dto.getMaxReminders() != null)        entity.setMaxReminders(dto.getMaxReminders());
        if (dto.getEnabled() != null)             entity.setEnabled(dto.getEnabled());

        entity = repository.save(entity);
        return ResponseEntity.ok(toDto(entity));
    }

    // ---- helpers ----

    private ReminderSettingsEntity defaultSettings() {
        ReminderSettingsEntity e = new ReminderSettingsEntity();
        e.setId(1);
        return e;
    }

    private ReminderSettingsDTO toDto(ReminderSettingsEntity e) {
        ReminderSettingsDTO dto = new ReminderSettingsDTO();
        dto.setInitialDelayHours(e.getInitialDelayHours());
        dto.setRepeatIntervalHours(e.getRepeatIntervalHours());
        dto.setMaxReminders(e.getMaxReminders());
        dto.setEnabled(e.getEnabled());
        return dto;
    }
}
