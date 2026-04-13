package org.spms.spmsbackend.logic.service;

import org.spms.spmsbackend.domain.notifications.Notification;
import org.spms.spmsbackend.domain.orders.Order;
import org.spms.spmsbackend.domain.orders.OrderSize;
import org.spms.spmsbackend.domain.orders.OrderStatus;
import org.spms.spmsbackend.domain.users.User;
import org.spms.spmsbackend.logic.interfaces.IOrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService implements IOrderService {

    private final NotificationService notificationService;

    public OrderService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public List<Order> getAllOrders() {
        return List.of();
    }

    @Override
    public Order getOrderById(int id) {
        User user = new User();
        user.setId(1);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setBuilding("Building A");

        LocalDateTime deliveredAt = LocalDateTime.now();

        OrderStatus status = new OrderStatus();
        status.setId(1);
        status.setName("READY_FOR_PICKUP");
        status.setDescription("Package has arrived and is ready for pickup.");

        Order order = new Order();
        order.setId(id);
        order.setEmployee(user);
        order.setTrackingNumber("TRK-123456");
        order.setSize(OrderSize.MEDIUM);
        order.setPickedUp(false);
        order.setDeliveredAt(deliveredAt);
        order.setPickupDeadline(calculatePickupDeadline(deliveredAt));
        order.setBuildingName("Building A");
        order.setStatus(status);

        return order;
    }

    private LocalDateTime calculatePickupDeadline(LocalDateTime deliveredAt) {
        return deliveredAt.plusDays(5);
    }

    @Override
    public Order getOrderByTrackingNumber(String trackingNumber) {
        return null;
    }

    @Override
    public List<Order> getOrdersForEmployee(int employeeId) {
        return List.of();
    }

    @Override
    public Order createOrder(int employeeId, String trackingNumber, OrderSize size) {
        return null;
    }

    @Override
    public Order markAsPickedUp(int orderId) {
        return null;
    }

    @Override
    public Order updateStatus(int orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);

        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        order.setStatus(newStatus);
        return order;
    }

    @Override
    public void deleteOrder(int id) {
    }

    @Override
    public Notification createArrivalNotification(int orderId) {
        Order order = getOrderById(orderId);

        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        return notificationService.createOrderArrivedNotification(order);
    }

    @Override
    public Notification createPickupReminderNotification(int orderId) {
        Order order = getOrderById(orderId);

        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        return notificationService.createPickupReminderNotification(order);
    }
}