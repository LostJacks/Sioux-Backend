package org.spms.spmsbackend.repository;

import org.spms.spmsbackend.entity.FloorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloorRepository extends JpaRepository<FloorEntity, Integer> {
}
