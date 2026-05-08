package org.spms.spmsbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Persisted notification record (added for US-03).
 *
 * <p>Each row represents a notification delivered to exactly one recipient.
 * Sioux/global package events fan out to multiple manager rows, each with
 * {@code scope = "GLOBAL"} so per-user read state still works.</p>
 *
 * <p>{@code scope}, {@code type} and {@code channel} are stored as strings
 * (not @Enumerated) for forward-compatibility with future enum values.</p>
 */
@Entity
@Table(name = "Notifications", schema = "dbo")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "recipient_user_id", nullable = false)
    @JsonIgnoreProperties({"building", "passwordHash"})
    private UserEntity recipient;

    @ManyToOne
    @JoinColumn(name = "package_id")
    @JsonIgnoreProperties({"employee", "assignedRow"})
    private PackageEntity relatedPackage;

    @Column(name = "scope", nullable = false, length = 20)
    private String scope;

    @Column(name = "type", nullable = false, length = 32)
    private String type;

    @Column(name = "channel", nullable = false, length = 16)
    private String channel;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean read;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (read == null) {
            read = false;
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public UserEntity getRecipient() { return recipient; }
    public void setRecipient(UserEntity recipient) { this.recipient = recipient; }

    public PackageEntity getRelatedPackage() { return relatedPackage; }
    public void setRelatedPackage(PackageEntity relatedPackage) { this.relatedPackage = relatedPackage; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getRead() { return read; }
    public void setRead(Boolean read) { this.read = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}
