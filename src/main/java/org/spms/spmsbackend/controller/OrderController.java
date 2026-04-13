package org.spms.spmsbackend.controller;

import org.spms.spmsbackend.domain.notifications.Notification;
import org.spms.spmsbackend.logic.interfaces.IOrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final IOrderService orderService;

    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{orderId}/arrival-notification")
    public Notification createArrivalNotification(@PathVariable int orderId) {
        return orderService.createArrivalNotification(orderId);
    }

    @PostMapping("/{orderId}/reminder-notification")
    public Notification createReminderNotification(@PathVariable int orderId) {
        return orderService.createPickupReminderNotification(orderId);
    }
}