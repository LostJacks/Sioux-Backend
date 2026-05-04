package org.spms.spmsbackend.logic.interfaces;

import org.spms.spmsbackend.domain.notifications.Notification;
import org.spms.spmsbackend.domain.orders.Order;
import org.spms.spmsbackend.domain.orders.OrderSize;
import org.spms.spmsbackend.domain.orders.OrderStatus;

import java.util.List;

public interface IOrderService {

    List<Order> getAllOrders();

    Order getOrderById(int id);

    Order getOrderByTrackingNumber(String trackingNumber);

    List<Order> getOrdersForEmployee(int employeeId);

    Order createOrder(int employeeId, String trackingNumber, OrderSize size);

    Order markAsPickedUp(int orderId);

    Order updateStatus(int orderId, OrderStatus newStatus);

    void deleteOrder(int id);

    /**
     * Trigger an arrival notification for the given order.
     *
     * @param orderId       the order/package id
     * @param targetUserIds optional manager subset for Sioux/unaddressed
     *                      packages. {@code null} or empty means "fan out
     *                      to all managers". Ignored for personal packages.
     */
    Notification createArrivalNotification(int orderId, List<Integer> targetUserIds);

    Notification createPickupReminderNotification(int orderId);
}

