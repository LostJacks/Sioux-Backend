package org.spms.spmsbackend.repository;

import org.spms.spmsbackend.entity.BuildingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildingRepository extends JpaRepository<BuildingEntity, Integer> {
}
