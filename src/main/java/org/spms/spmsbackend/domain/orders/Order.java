package org.spms.spmsbackend.domain.orders;

import org.spms.spmsbackend.domain.users.User;
import java.time.LocalDateTime;

public class Order {
    private int id;
    private User employee;
    private String trackingNumber;
    private OrderSize size;
    private boolean pickedUp;
    private LocalDateTime pickedUpAt;
    private OrderStatus status;
    private LocalDateTime deliveredAt;

    public Order(int id, User employee, String trackingNumber, OrderSize size, boolean pickedUp, LocalDateTime pickedUpAt, OrderStatus status, LocalDateTime deliveredAt) {
        this.id = id;
        this.employee = employee;
        this.trackingNumber = trackingNumber;
        this.size = size;
        this.pickedUp = pickedUp;
        this.pickedUpAt = pickedUpAt;
        this.status = status;
        this.deliveredAt = deliveredAt;
    }

    public Order() {
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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Order)) return false;
        final Order other = (Order) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getId() != other.getId()) return false;
        final Object this$employee = this.getEmployee();
        final Object other$employee = other.getEmployee();
        if (this$employee == null ? other$employee != null : !this$employee.equals(other$employee)) return false;
        final Object this$trackingNumber = this.getTrackingNumber();
        final Object other$trackingNumber = other.getTrackingNumber();
        if (this$trackingNumber == null ? other$trackingNumber != null : !this$trackingNumber.equals(other$trackingNumber))
            return false;
        final Object this$size = this.getSize();
        final Object other$size = other.getSize();
        if (this$size == null ? other$size != null : !this$size.equals(other$size)) return false;
        if (this.isPickedUp() != other.isPickedUp()) return false;
        final Object this$pickedUpAt = this.getPickedUpAt();
        final Object other$pickedUpAt = other.getPickedUpAt();
        if (this$pickedUpAt == null ? other$pickedUpAt != null : !this$pickedUpAt.equals(other$pickedUpAt))
            return false;
        final Object this$status = this.getStatus();
        final Object other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) return false;
        final Object this$deliveredAt = this.getDeliveredAt();
        final Object other$deliveredAt = other.getDeliveredAt();
        if (this$deliveredAt == null ? other$deliveredAt != null : !this$deliveredAt.equals(other$deliveredAt))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Order;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getId();
        final Object $employee = this.getEmployee();
        result = result * PRIME + ($employee == null ? 43 : $employee.hashCode());
        final Object $trackingNumber = this.getTrackingNumber();
        result = result * PRIME + ($trackingNumber == null ? 43 : $trackingNumber.hashCode());
        final Object $size = this.getSize();
        result = result * PRIME + ($size == null ? 43 : $size.hashCode());
        result = result * PRIME + (this.isPickedUp() ? 79 : 97);
        final Object $pickedUpAt = this.getPickedUpAt();
        result = result * PRIME + ($pickedUpAt == null ? 43 : $pickedUpAt.hashCode());
        final Object $status = this.getStatus();
        result = result * PRIME + ($status == null ? 43 : $status.hashCode());
        final Object $deliveredAt = this.getDeliveredAt();
        result = result * PRIME + ($deliveredAt == null ? 43 : $deliveredAt.hashCode());
        return result;
    }

    public String toString() {
        return "Order(id=" + this.getId() + ", employee=" + this.getEmployee() + ", trackingNumber=" + this.getTrackingNumber() + ", size=" + this.getSize() + ", pickedUp=" + this.isPickedUp() + ", pickedUpAt=" + this.getPickedUpAt() + ", status=" + this.getStatus() + ", deliveredAt=" + this.getDeliveredAt() + ")";
    }
}
