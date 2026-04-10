package org.spms.spmsbackend.dtos;

import org.spms.spmsbackend.domain.orders.OrderSize;
import org.spms.spmsbackend.domain.orders.OrderStatus;
import org.spms.spmsbackend.domain.users.User;

import java.time.LocalDateTime;

public class OrderDTO {
    private int id;
    private User employee;
    private String trackingNumber;
    private OrderSize size;
    private boolean pickedUp;
    private LocalDateTime pickedUpAt;
    private OrderStatus status;
    private LocalDateTime deliveredAt;

    public OrderDTO(int id, User employee, String trackingNumber, OrderSize size, boolean pickedUp, LocalDateTime pickedUpAt, OrderStatus status, LocalDateTime deliveredAt) {
        this.id = id;
        this.employee = employee;
        this.trackingNumber = trackingNumber;
        this.size = size;
        this.pickedUp = pickedUp;
        this.pickedUpAt = pickedUpAt;
        this.status = status;
        this.deliveredAt = deliveredAt;
    }

    public OrderDTO() {
    }

    public int getId() {
        return this.id;
    }

    public User getEmployee() {
        return this.employee;
    }

    public String getTrackingNumber() {
        return this.trackingNumber;
    }

    public OrderSize getSize() {
        return this.size;
    }

    public boolean isPickedUp() {
        return this.pickedUp;
    }

    public LocalDateTime getPickedUpAt() {
        return this.pickedUpAt;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public LocalDateTime getDeliveredAt() {
        return this.deliveredAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public void setSize(OrderSize size) {
        this.size = size;
    }

    public void setPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

    public void setPickedUpAt(LocalDateTime pickedUpAt) {
        this.pickedUpAt = pickedUpAt;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
}
