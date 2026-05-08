package org.spms.spmsbackend.controllers;

import org.spms.spmsbackend.domain.notifications.Notification;
import org.spms.spmsbackend.exceptions.NotFoundException;
import org.spms.spmsbackend.logic.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Notification REST endpoints (US-03).
 *
 * <p>{@code /employee/{id}} returns the user's full personal feed (Mine
 * tab). {@code /global/{managerId}} returns just that manager's GLOBAL
 * scope copies (Global tab). {@code /employee/{id}/since?afterId=N}
 * supports the polling client.</p>
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<List<Notification>> getForEmployee(@PathVariable int id) {
        return ResponseEntity.ok(notificationService.getForEmployee(id));
    }

    @GetMapping("/employee/{id}/since")
    public ResponseEntity<List<Notification>> getNewerThan(@PathVariable int id,
                                                           @RequestParam(name = "afterId", defaultValue = "0") int afterId) {
        return ResponseEntity.ok(notificationService.getNewerThan(id, afterId));
    }

    @GetMapping("/global/{managerId}")
    public ResponseEntity<List<Notification>> getGlobalForManager(@PathVariable int managerId) {
        return ResponseEntity.ok(notificationService.getGlobalForManager(managerId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable int id) {
        try {
            return ResponseEntity.ok(notificationService.markAsRead(id));
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
