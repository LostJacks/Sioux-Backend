package org.spms.spmsbackend.dtos;

import java.util.List;

public class BuildingTreeDTO {
    private Integer id;
    private String name;
    private List<FloorDTO> floors;

    public BuildingTreeDTO(Integer id, String name, List<FloorDTO> floors) {
        this.id = id;
        this.name = name;
        this.floors = floors;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public List<FloorDTO> getFloors() { return floors; }

    public static class FloorDTO {
        private Integer id;
        private Integer floorNumber;
        private List<ShelfDTO> shelves;

        public FloorDTO(Integer id, Integer floorNumber, List<ShelfDTO> shelves) {
            this.id = id;
            this.floorNumber = floorNumber;
            this.shelves = shelves;
        }

        public Integer getId() { return id; }
        public Integer getFloorNumber() { return floorNumber; }
        public List<ShelfDTO> getShelves() { return shelves; }
    }

    public static class ShelfDTO {
        private Integer id;
        private Integer shelfNumber;
        private List<RowDTO> rows;

        public ShelfDTO(Integer id, Integer shelfNumber, List<RowDTO> rows) {
            this.id = id;
            this.shelfNumber = shelfNumber;
            this.rows = rows;
        }

        public Integer getId() { return id; }
        public Integer getShelfNumber() { return shelfNumber; }
        public List<RowDTO> getRows() { return rows; }
    }

    public static class RowDTO {
        private Integer id;
        private Integer rowNumber;

        public RowDTO(Integer id, Integer rowNumber) {
            this.id = id;
            this.rowNumber = rowNumber;
        }

        public Integer getId() { return id; }
        public Integer getRowNumber() { return rowNumber; }
    }
}
