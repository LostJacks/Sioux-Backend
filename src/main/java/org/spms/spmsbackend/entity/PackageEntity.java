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

}


