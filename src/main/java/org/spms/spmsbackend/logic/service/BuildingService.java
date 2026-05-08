package org.spms.spmsbackend.logic.service;

import org.spms.spmsbackend.dtos.BuildingTreeDTO;
import org.spms.spmsbackend.dtos.CreateBuildingRequest;
import org.spms.spmsbackend.entity.*;
import org.spms.spmsbackend.repository.BuildingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        floor.setFloorNumber(1);

        List<ShelfEntity> shelves = new ArrayList<>();

        int shelfIndex = 1;
        for (CreateBuildingRequest.ShelfRequest shelfReq : request.getShelves()) {
            ShelfEntity shelf = new ShelfEntity();
            shelf.setFloor(floor);
            shelf.setShelfNumber(shelfIndex++);
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

    @Transactional(readOnly = true)
    public List<BuildingTreeDTO> getBuildingTree() {
        return buildingRepository.findAll().stream()
                .map(b -> {
                    List<BuildingTreeDTO.FloorDTO> floors = b.getFloors().stream()
                            .map(f -> {
                                List<BuildingTreeDTO.ShelfDTO> shelves = f.getShelves().stream()
                                        .map(s -> {
                                            List<BuildingTreeDTO.RowDTO> rows = s.getRows().stream()
                                                    .map(r -> new BuildingTreeDTO.RowDTO(r.getId(), r.getRowNumber()))
                                                    .toList();
                                            return new BuildingTreeDTO.ShelfDTO(s.getId(), s.getShelfNumber(), rows);
                                        })
                                        .toList();
                                return new BuildingTreeDTO.FloorDTO(f.getId(), f.getFloorNumber(), shelves);
                            })
                            .toList();
                    return new BuildingTreeDTO(b.getId(), b.getName(), floors);
                })
                .toList();
    }
}