package org.spms.spmsbackend.domain.notifications;

import java.time.LocalDateTime;

/**
 * Domain notification model. The {@code packageId}, {@code scope} and
 * {@code channel} fields were added for US-03; existing code that builds
 * a Notification with only id/userId/title/message/type/read/createdAt
 * keeps working — scope defaults to PERSONAL and channel to IN_APP.
 */
public class Notification {
    private int id;
    private int userId;
    private Integer packageId;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationScope scope = NotificationScope.PERSONAL;
    private NotificationChannel channel = NotificationChannel.IN_APP;
    private boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public Notification() {
    }

    public Notification(int id, int userId, String title, String message, NotificationType type, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.read = read;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public NotificationType getType() {
        return type;
    }

    public NotificationScope getScope() {
        return scope;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public boolean isRead() {
        return read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public void setScope(NotificationScope scope) {
        this.scope = scope;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}
