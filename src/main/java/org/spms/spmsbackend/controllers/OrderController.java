package org.spms.spmsbackend.controllers;

import lombok.AllArgsConstructor;
import org.spms.spmsbackend.domain.notifications.Notification;
import org.spms.spmsbackend.domain.orders.Order;
import org.spms.spmsbackend.dtos.ArrivalNotificationRequest;
import org.spms.spmsbackend.dtos.StorePackageRequest;
import org.spms.spmsbackend.exceptions.NotFoundException;
import org.spms.spmsbackend.logic.interfaces.IOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private IOrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(orderService.getOrderById(id));
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Order>> getOrdersForEmployee(@PathVariable int employeeId) {
        return ResponseEntity.ok(orderService.getOrdersForEmployee(employeeId));
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<Order> getOrderByTrackingNumber(@PathVariable String trackingNumber) {
        try {
            return ResponseEntity.ok(orderService.getOrderByTrackingNumber(trackingNumber));
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/arrival-notification")
    public ResponseEntity<Notification> createArrivalNotification(
            @PathVariable int id,
            @RequestBody(required = false) ArrivalNotificationRequest body
    ) {
        try {
            List<Integer> targetUserIds = body != null ? body.getTargetUserIds() : null;
            return ResponseEntity.ok(orderService.createArrivalNotification(id, targetUserIds));
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/reminder-notification")
    public ResponseEntity<Notification> createPickupReminderNotification(@PathVariable int id) {
        try {
            return ResponseEntity.ok(orderService.createPickupReminderNotification(id));
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/store")
    public ResponseEntity<Order> storePackage(@RequestBody StorePackageRequest request) {
        try {
            return ResponseEntity.ok(orderService.storePackage(
                    request.getTrackingNumber(),
                    request.getEmployeeId(),
                    request.getRowId(),
                    request.getRecipientName()
            ));
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/broadcast")
    public ResponseEntity<Notification> broadcastUnknownPackage(
            @PathVariable int id,
            @RequestBody java.util.Map<String, String> body) {
        try {
            String message = body.getOrDefault("message", "An unaddressed package has arrived. Please contact the receptionist if it may be yours.");
            return ResponseEntity.ok(orderService.broadcastUnknownPackage(id, message));
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}/pickup")
    public ResponseEntity<Order> markAsPickedUp(@PathVariable int id) {
        try {
            return ResponseEntity.ok(orderService.markAsPickedUp(id));
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
