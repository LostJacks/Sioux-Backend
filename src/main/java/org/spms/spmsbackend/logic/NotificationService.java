package org.spms.spmsbackend.logic;

import org.spms.spmsbackend.domain.notifications.Notification;
import org.spms.spmsbackend.domain.notifications.NotificationType;
import org.spms.spmsbackend.domain.orders.Order;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

@Service
public class NotificationService {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    public Notification createOrderArrivedNotification(Order order) {
        Notification notification = new Notification();
        notification.setUserId(order.getEmployee().getId());
        notification.setTitle("Your package has arrived");
        notification.setMessage(
                "Your package has arrived at " + order.getBuildingName()
                        + ". Please collect it before "
                        + order.getPickupDeadline().format(FORMATTER) + "."
        );
        notification.setType(NotificationType.ORDER_ARRIVED);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notification;
    }

    public Notification createPickupReminderNotification(Order order) {
        Notification notification = new Notification();
        notification.setUserId(order.getEmployee().getId());
        notification.setTitle("Pickup reminder");
        notification.setMessage(
                "Your package at " + order.getBuildingName()
                        + " must be collected within 2 days. Final pickup date: "
                        + order.getPickupDeadline().format(FORMATTER) + "."
        );
        notification.setType(NotificationType.PICKUP_REMINDER);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notification;
    }
}