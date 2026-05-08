package org.spms.spmsbackend.repository;

import org.spms.spmsbackend.entity.ShelfRowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelfRowRepository extends JpaRepository<ShelfRowEntity, Integer> {
}
