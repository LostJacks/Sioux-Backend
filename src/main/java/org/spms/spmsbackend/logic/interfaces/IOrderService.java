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

    Order storePackage(String trackingNumber, Integer employeeId, Integer rowId);

    Order markAsPickedUp(int orderId);

    Order updateStatus(int orderId, OrderStatus newStatus);

    void deleteOrder(int id);

    Notification createArrivalNotification(int orderId);
    Notification createPickupReminderNotification(int orderId);
}
