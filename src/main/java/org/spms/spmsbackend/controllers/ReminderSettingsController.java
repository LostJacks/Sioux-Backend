package org.spms.spmsbackend.controllers;

import org.spms.spmsbackend.entity.ReminderSettingsEntity;
import org.spms.spmsbackend.logic.service.ReminderSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reminder-settings")
public class ReminderSettingsController {

    private final ReminderSettingsService service;

    public ReminderSettingsController(ReminderSettingsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ReminderSettingsEntity> getSettings() {
        return ResponseEntity.ok(service.getSettings());
    }

    @PutMapping
    public ResponseEntity<ReminderSettingsEntity> updateSettings(
            @RequestBody ReminderSettingsEntity settings) {
        return ResponseEntity.ok(service.updateSettings(settings));
    }
}
