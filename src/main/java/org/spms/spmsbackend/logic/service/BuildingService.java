package org.spms.spmsbackend.logic.service;

import org.spms.spmsbackend.dtos.BuildingTreeDTO;
import org.spms.spmsbackend.dtos.CreateBuildingRequest;
import org.spms.spmsbackend.entity.*;
import org.spms.spmsbackend.repository.BuildingRepository;
import org.spms.spmsbackend.repository.FloorRepository;
import org.spms.spmsbackend.repository.ShelfRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final ShelfRepository shelfRepository;

    public BuildingService(
            BuildingRepository buildingRepository,
            FloorRepository floorRepository,
            ShelfRepository shelfRepository
    ) {
        this.buildingRepository = buildingRepository;
        this.floorRepository = floorRepository;
        this.shelfRepository = shelfRepository;
    }

    public BuildingEntity createBuilding(CreateBuildingRequest request) {
        BuildingEntity building = new BuildingEntity();
        building.setName(request.getName());
        building.setAddress(request.getAddress());

        FloorEntity floor = new FloorEntity();
        floor.setBuilding(building);
        floor.setFloorNumber(request.getFloorNumber() != null ? request.getFloorNumber() : 1);
        floor.setShelves(buildShelves(floor, request.getShelves(), 1));
        building.setFloors(List.of(floor));

        return buildingRepository.save(building);
    }

    @Transactional
    public BuildingEntity updateBuilding(Integer buildingId, CreateBuildingRequest request) {
        BuildingEntity building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("Building not found: " + buildingId));

        building.setName(request.getName());
        building.setAddress(request.getAddress());

        FloorEntity floor = building.getFloors().stream().findFirst().orElseGet(() -> {
            FloorEntity created = new FloorEntity();
            created.setBuilding(building);
            created.setShelves(new ArrayList<>());
            building.setFloors(List.of(created));
            return created;
        });

        if (request.getFloorNumber() != null) {
            floor.setFloorNumber(request.getFloorNumber());
        }
        floorRepository.save(floor);

        int nextShelfNumber = floor.getShelves().stream()
                .map(ShelfEntity::getShelfNumber)
                .filter(n -> n != null)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        List<ShelfEntity> newShelves = buildShelves(floor, request.getShelves(), nextShelfNumber);
        if (!newShelves.isEmpty()) {
            floor.getShelves().addAll(newShelves);
        }

        return buildingRepository.save(building);
    }

    private List<ShelfEntity> buildShelves(
            FloorEntity floor,
            List<CreateBuildingRequest.ShelfRequest> shelfRequests,
            int startShelfNumber
    ) {
        List<ShelfEntity> shelves = new ArrayList<>();
        if (shelfRequests == null) {
            return shelves;
        }

        int shelfIndex = startShelfNumber;
        for (CreateBuildingRequest.ShelfRequest shelfReq : shelfRequests) {
            if (shelfReq == null || shelfReq.getRowCount() <= 0) continue;

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
        return shelves;
    }

    public List<BuildingEntity> getAllBuildings() {
        return buildingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean shelfHasPendingPackages(Integer shelfId) {
        ShelfEntity shelf = shelfRepository.findById(shelfId)
                .orElseThrow(() -> new RuntimeException("Shelf not found: " + shelfId));
        return shelf.getRows().stream()
                .flatMap(row -> row.getPackages().stream())
                .anyMatch(pkg -> !Boolean.TRUE.equals(pkg.getPickedUp()));
    }

    @Transactional
    public void deleteShelf(Integer shelfId) {
        shelfRepository.deleteById(shelfId);
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
                    return new BuildingTreeDTO(b.getId(), b.getName(), b.getAddress(), floors);
                })
                .toList();
    }
}