package org.spms.spmsbackend.logic.interfaces;

import org.spms.spmsbackend.domain.Order;
import org.spms.spmsbackend.domain.OrderSize;
import org.spms.spmsbackend.domain.OrderStatus;

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

}
