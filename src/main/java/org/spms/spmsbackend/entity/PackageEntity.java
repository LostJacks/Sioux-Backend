package org.spms.spmsbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Packages", schema = "dbo")
public class PackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnoreProperties({"building"})   // prevent recursion employee → building → floors
    private UserEntity employee;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    // now allowed to be NULL for pre-registered packages
    @Column(name = "size", length = 20)
    private String size;

    @ManyToOne
    @JoinColumn(name = "assigned_row_id")
    @JsonIgnoreProperties({"packages"})   // prevent recursion row → packages → row
    private ShelfRowEntity assignedRow;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "picked_up")
    private Boolean pickedUp;

    @Column(name = "picked_up_at")
    private LocalDateTime pickedUpAt;

    @Column(name = "status")
    private String status;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    /**
     * Free-text recipient as printed on the parcel. May be {@code null} when
     * the carrier left no recipient (e.g. addressed only to "Sioux"). Added
     * for US-03 to support classifying company-addressed parcels.
     */
    @Column(name = "recipient_name", length = 255)
    private String recipientName;

    /**
     * EMPLOYEE / COMPANY / UNKNOWN. Stored as a string so future values can
     * be added without migrations. Added for US-03.
     */
    @Column(name = "addressed_to", length = 20)
    private String addressedTo;

    /** How many automated pickup reminders have been sent for this package. */
    @Column(name = "reminder_count", nullable = false)
    private Integer reminderCount = 0;

    /** Timestamp of the most recent automated reminder (null = none sent yet). */
    @Column(name = "last_reminder_sent_at")
    private LocalDateTime lastReminderSentAt;


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public UserEntity getEmployee() { return employee; }
    public void setEmployee(UserEntity employee) { this.employee = employee; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public ShelfRowEntity getAssignedRow() { return assignedRow; }
    public void setAssignedRow(ShelfRowEntity assignedRow) { this.assignedRow = assignedRow; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public Boolean getPickedUp() { return pickedUp; }
    public void setPickedUp(Boolean pickedUp) { this.pickedUp = pickedUp; }

    public LocalDateTime getPickedUpAt() { return pickedUpAt; }
    public void setPickedUpAt(LocalDateTime pickedUpAt) { this.pickedUpAt = pickedUpAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getAddressedTo() { return addressedTo; }
    public void setAddressedTo(String addressedTo) { this.addressedTo = addressedTo; }

    public Integer getReminderCount() { return reminderCount == null ? 0 : reminderCount; }
    public void setReminderCount(Integer reminderCount) { this.reminderCount = reminderCount; }

    public LocalDateTime getLastReminderSentAt() { return lastReminderSentAt; }
    public void setLastReminderSentAt(LocalDateTime lastReminderSentAt) { this.lastReminderSentAt = lastReminderSentAt; }
}


