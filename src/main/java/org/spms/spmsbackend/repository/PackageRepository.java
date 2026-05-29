package org.spms.spmsbackend.repository;

import org.spms.spmsbackend.entity.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<PackageEntity, Integer> {
    Optional<PackageEntity> findByTrackingNumber(String trackingNumber);
    List<PackageEntity> findByEmployee_Id(Integer employeeId);

    /**
     * Returns packages that have not been picked up and were stored before
     * the given threshold (i.e. older than N days). Used by the reminder scheduler.
     */
    List<PackageEntity> findByPickedUpFalseAndAssignedAtBefore(LocalDateTime threshold);
}