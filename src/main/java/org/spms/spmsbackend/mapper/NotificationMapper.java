package org.spms.spmsbackend.mapper;

import org.spms.spmsbackend.domain.notifications.Notification;
import org.spms.spmsbackend.domain.notifications.NotificationChannel;
import org.spms.spmsbackend.domain.notifications.NotificationScope;
import org.spms.spmsbackend.domain.notifications.NotificationType;
import org.spms.spmsbackend.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toDomain(NotificationEntity entity) {
        Notification n = new Notification();
        n.setId(entity.getId() != null ? entity.getId() : 0);
        n.setUserId(entity.getRecipient() != null && entity.getRecipient().getId() != null
                ? entity.getRecipient().getId()
                : 0);
        n.setPackageId(entity.getRelatedPackage() != null
                ? entity.getRelatedPackage().getId()
                : null);
        n.setTitle(entity.getTitle());
        n.setMessage(entity.getMessage());
        n.setType(parseType(entity.getType()));
        n.setScope(parseScope(entity.getScope()));
        n.setChannel(parseChannel(entity.getChannel()));
        n.setRead(Boolean.TRUE.equals(entity.getRead()));
        n.setCreatedAt(entity.getCreatedAt());
        n.setReadAt(entity.getReadAt());
        return n;
    }

    private NotificationType parseType(String raw) {
        if (raw == null) return null;
        try {
            return NotificationType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private NotificationScope parseScope(String raw) {
        if (raw == null) return NotificationScope.PERSONAL;
        try {
            return NotificationScope.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return NotificationScope.PERSONAL;
        }
    }

    private NotificationChannel parseChannel(String raw) {
        if (raw == null) return NotificationChannel.IN_APP;
        try {
            return NotificationChannel.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return NotificationChannel.IN_APP;
        }
    }
}
