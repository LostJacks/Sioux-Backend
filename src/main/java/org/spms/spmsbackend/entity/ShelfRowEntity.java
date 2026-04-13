package org.spms.spmsbackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "ShelfRows", schema = "dbo")
public class ShelfRowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "row_id")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shelf_id")
    @JsonIgnoreProperties({"rows"})   // prevent recursion when serializing shelf → rows → shelf
    private ShelfEntity shelf;

    @Column(name = "row_number")
    private Integer rowNumber;

    // DB: decimal(5,2) default 100.00
    @Column(name = "capacity", precision = 5, scale = 2)
    private BigDecimal capacity;

    @Column(name = "qr_code", length = 50)
    private String qrCode;

    @OneToMany(mappedBy = "assignedRow", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"assignedRow"})   // prevent recursion when serializing packages → row → packages
    private List<PackageEntity> packages = new ArrayList<>();

    // Getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public ShelfEntity getShelf() { return shelf; }
    public void setShelf(ShelfEntity shelf) { this.shelf = shelf; }

    public Integer getRowNumber() { return rowNumber; }
    public void setRowNumber(Integer rowNumber) { this.rowNumber = rowNumber; }

    public BigDecimal getCapacity() { return capacity; }
    public void setCapacity(BigDecimal capacity) { this.capacity = capacity; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public List<PackageEntity> getPackages() { return packages; }
    public void setPackages(List<PackageEntity> packages) { this.packages = packages; }
}
