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

    /**
     * Returns all packages that have not been picked up yet AND belong to an
     * identified employee (i.e. personal packages that can receive a reminder).
     * Used by the reminder scheduler.
     */
    List<PackageEntity> findByPickedUpFalseAndEmployeeIsNotNull();
}