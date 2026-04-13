package org.spms.spmsbackend.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Shelves", schema = "dbo")
public class ShelfEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shelf_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "floor_id")
    @JsonIgnoreProperties({"shelves"})   // prevent recursion when serializing floor → shelves
    private FloorEntity floor;

    @Column(name = "shelf_number")
    private Integer shelfNumber;

    @Column(name = "material")
    private String material;

    @Column(name = "capacity")
    private Integer capacity;

    @Transient
    @Column(name = "rows_count")
    private Integer rowsCount;

    @OneToMany(mappedBy = "shelf", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"shelf"})     // prevent recursion when serializing rows → shelf
    private List<ShelfRowEntity> rows = new ArrayList<>();

    // Getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public FloorEntity getFloor() { return floor; }
    public void setFloor(FloorEntity floor) { this.floor = floor; }

    public Integer getShelfNumber() { return shelfNumber; }
    public void setShelfNumber(Integer shelfNumber) { this.shelfNumber = shelfNumber; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getRowsCount() { return rowsCount; }
    public void setRowsCount(Integer rowsCount) { this.rowsCount = rowsCount; }

    public List<ShelfRowEntity> getRows() { return rows; }
    public void setRows(List<ShelfRowEntity> rows) { this.rows = rows; }
}

