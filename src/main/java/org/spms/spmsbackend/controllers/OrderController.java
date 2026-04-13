package org.spms.spmsbackend.controllers;

import org.spms.spmsbackend.dtos.GetOrderResponse;
import org.spms.spmsbackend.domain.notifications.Notification;
import org.spms.spmsbackend.domain.orders.Order;
import org.spms.spmsbackend.logic.interfaces.IOrderService;
import org.spms.spmsbackend.mapper.OrderMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final IOrderService orderService;

    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public GetOrderResponse getOrders() {
        List<Order> orderList = orderService.getAllOrders();

        return OrderMapper.mapOrderToGetOrderResponse(orderList);
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
