package org.spms.spmsbackend.logic;

import org.spms.spmsbackend.domain.orders.Order;
import org.spms.spmsbackend.domain.orders.OrderSize;
import org.spms.spmsbackend.domain.orders.OrderStatus;
import org.spms.spmsbackend.logic.interfaces.IOrderService;

import java.util.List;

public class OrderService implements IOrderService {

    @Override
    public List<Order> getAllOrders() {
        return List.of();
    }

    @Override
    public Order getOrderById(int id) {
        return null;
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
        return null;
    }

    @Override
    public void deleteOrder(int id) {

    }
}
