package org.spms.spmsbackend.logic.service;

import org.spms.spmsbackend.dtos.CreateBuildingRequest;
import org.spms.spmsbackend.entity.*;
import org.spms.spmsbackend.repository.BuildingRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BuildingService {

    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    public BuildingEntity createBuilding(CreateBuildingRequest request) {
        BuildingEntity building = new BuildingEntity();
        building.setName(request.getName());
        building.setAddress(request.getAddress());

        // TEMP: create 1 floor (since your model requires it)
        FloorEntity floor = new FloorEntity();
        floor.setBuilding(building);

        List<ShelfEntity> shelves = new ArrayList<>();

        for (CreateBuildingRequest.ShelfRequest shelfReq : request.getShelves()) {
            ShelfEntity shelf = new ShelfEntity();
            shelf.setFloor(floor);
            shelf.setShelfNumber(null); // optional
            shelf.setRows(new ArrayList<>());

            List<ShelfRowEntity> rows = new ArrayList<>();

            for (int i = 1; i <= shelfReq.getRowCount(); i++) {
                ShelfRowEntity row = new ShelfRowEntity();
                row.setShelf(shelf);
                row.setRowNumber(i);
                rows.add(row);
            }

            shelf.setRows(rows);
            shelves.add(shelf);
        }

        floor.setShelves(shelves);
        building.setFloors(List.of(floor));

        return buildingRepository.save(building);
    }

    public List<BuildingEntity> getAllBuildings() {
        return buildingRepository.findAll();
    }
}