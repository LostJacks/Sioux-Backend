package org.spms.spmsbackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Floors", schema = "dbo")
public class FloorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "floor_id")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "building_id")
    @JsonIgnoreProperties({"floors"})   // prevent recursion when serializing building → floors
    private BuildingEntity building;

    @Column(name = "floor_number")
    private Integer floorNumber;

    @Column(name = "floor_name")
    private String floorName;

    @Column(name = "area", precision = 10, scale = 2)
    private BigDecimal area;

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"floor"})    // prevent recursion when serializing shelves → floor
    private List<ShelfEntity> shelves = new ArrayList<>();

    // Getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public BuildingEntity getBuilding() { return building; }
    public void setBuilding(BuildingEntity building) { this.building = building; }

    public Integer getFloorNumber() { return floorNumber; }
    public void setFloorNumber(Integer floorNumber) { this.floorNumber = floorNumber; }

    public String getFloorName() { return floorName; }
    public void setFloorName(String floorName) { this.floorName = floorName; }

    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }

    public List<ShelfEntity> getShelves() { return shelves; }
    public void setShelves(List<ShelfEntity> shelves) { this.shelves = shelves; }
}
