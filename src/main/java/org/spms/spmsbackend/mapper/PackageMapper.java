package org.spms.spmsbackend.mapper;

import org.spms.spmsbackend.domain.orders.AddressedTo;
import org.spms.spmsbackend.domain.orders.Order;
import org.spms.spmsbackend.domain.orders.OrderSize;
import org.spms.spmsbackend.domain.orders.OrderStatus;
import org.spms.spmsbackend.domain.users.Role;
import org.spms.spmsbackend.domain.users.User;
import org.spms.spmsbackend.entity.BuildingEntity;
import org.spms.spmsbackend.entity.FloorEntity;
import org.spms.spmsbackend.entity.PackageEntity;
import org.spms.spmsbackend.entity.ShelfEntity;
import org.spms.spmsbackend.entity.ShelfRowEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PackageMapper {

    public Order toDomain(PackageEntity entity) {
        Order order = new Order();
        order.setId(entity.getId());

        if (entity.getEmployee() != null) {
            User user = new User();
            user.setId(entity.getEmployee().getId());
            user.setFirstName(entity.getEmployee().getFirstName());
            user.setLastName(entity.getEmployee().getLastName());

            if (entity.getEmployee().getRole() != null) {
                user.setRole(Role.valueOf(entity.getEmployee().getRole().toUpperCase()));
            }

            if (entity.getEmployee().getBuilding() != null) {
                user.setBuilding(entity.getEmployee().getBuilding().getName());
                order.setBuildingName(entity.getEmployee().getBuilding().getName());
            }

            order.setEmployee(user);
        }

        order.setTrackingNumber(entity.getTrackingNumber());

        if (entity.getSize() != null) {
            order.setSize(parseSize(entity.getSize()));
        }

        order.setPickedUp(Boolean.TRUE.equals(entity.getPickedUp()));
        order.setPickedUpAt(entity.getPickedUpAt());

        OrderStatus status = new OrderStatus();
        status.setId(0);
        status.setName(entity.getStatus() != null ? entity.getStatus() : "REGISTERED");
        status.setDescription(entity.getStatus() != null ? entity.getStatus() : "Registered");
        order.setStatus(status);

        if (entity.getDeliveryDate() != null) {
            LocalDateTime deliveredAt = entity.getDeliveryDate().atStartOfDay();
            order.setDeliveredAt(deliveredAt);
            order.setPickupDeadline(deliveredAt.plusDays(5));
        }

        // US-03: Sioux package classification fields.
        order.setRecipientName(entity.getRecipientName());
        order.setAddressedTo(parseAddressedTo(entity.getAddressedTo()));

        // develop: human-readable shelf-row location string.
        if (entity.getAssignedRow() != null) {
            ShelfRowEntity row = entity.getAssignedRow();
            ShelfEntity shelf = row.getShelf();
            FloorEntity floor = shelf != null ? shelf.getFloor() : null;
            BuildingEntity building = floor != null ? floor.getBuilding() : null;

            StringBuilder loc = new StringBuilder();
            if (building != null) loc.append(building.getName());
            if (floor != null) loc.append(", Floor ").append(floor.getFloorNumber());
            if (shelf != null) loc.append(", Shelf ").append(shelf.getShelfNumber());
            loc.append(", Row ").append(row.getRowNumber());
            order.setRowLocation(loc.toString());
        }

        return order;
    }

    private AddressedTo parseAddressedTo(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return AddressedTo.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return AddressedTo.UNKNOWN;
        }
    }

    private OrderSize parseSize(String size) {
        return switch (size.trim().toUpperCase()) {
            case "VERY_SMALL" -> OrderSize.VERY_SMALL;
            case "SMALL" -> OrderSize.SMALL;
            case "MEDIUM" -> OrderSize.MEDIUM;
            case "LARGE" -> OrderSize.LARGE;
            case "VERY_LARGE" -> OrderSize.VERY_LARGE;
            default -> OrderSize.MEDIUM;
        };
    }
}
