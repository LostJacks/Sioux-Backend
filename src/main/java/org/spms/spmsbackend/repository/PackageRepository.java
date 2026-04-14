package org.spms.spmsbackend.repository;

import org.spms.spmsbackend.entity.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<PackageEntity, Integer> {
    Optional<PackageEntity> findByTrackingNumber(String trackingNumber);
    List<PackageEntity> findByEmployee_Id(Integer employeeId);
}