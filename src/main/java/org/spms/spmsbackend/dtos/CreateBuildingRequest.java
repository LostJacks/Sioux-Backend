package org.spms.spmsbackend.dtos;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateBuildingRequest {
    private String name;
    private String address;
    private Integer floorNumber;
    private List<ShelfRequest> shelves;

    @Getter
    public static class ShelfRequest {
        private String name;
        private int rowCount;
    }
}