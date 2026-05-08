package org.spms.spmsbackend.repository;

import org.spms.spmsbackend.entity.ShelfEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelfRepository extends JpaRepository<ShelfEntity, Integer> {
}