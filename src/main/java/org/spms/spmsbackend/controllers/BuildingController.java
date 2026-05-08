package org.spms.spmsbackend.controllers;

import org.spms.spmsbackend.dtos.BuildingTreeDTO;
import org.spms.spmsbackend.dtos.CreateBuildingRequest;
import org.spms.spmsbackend.entity.BuildingEntity;
import org.spms.spmsbackend.logic.service.BuildingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/buildings")
@CrossOrigin(origins = "http://localhost:5173")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @PostMapping
    public BuildingEntity createBuilding(@RequestBody CreateBuildingRequest request) {
        return buildingService.createBuilding(request);
    }

    @GetMapping
    public List<BuildingEntity> getBuildings() {
        return buildingService.getAllBuildings();
    }

    @GetMapping("/full")
    public List<BuildingTreeDTO> getBuildingTree() {
        return buildingService.getBuildingTree();
    }

    @GetMapping("/shelves/{shelfId}/check")
    public ResponseEntity<Map<String, Boolean>> checkShelf(@PathVariable Integer shelfId) {
        boolean hasPending = buildingService.shelfHasPendingPackages(shelfId);
        return ResponseEntity.ok(Map.of("hasPendingPackages", hasPending));
    }

    @DeleteMapping("/shelves/{shelfId}")
    public ResponseEntity<Void> deleteShelf(@PathVariable Integer shelfId) {
        buildingService.deleteShelf(shelfId);
        return ResponseEntity.noContent().build();
    }
}