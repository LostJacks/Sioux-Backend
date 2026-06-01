package org.spms.spmsbackend.repository;

import org.spms.spmsbackend.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Integer> {

    /** Personal notification feed for an employee, newest first. */
    List<NotificationEntity> findByRecipient_IdOrderByCreatedAtDesc(Integer recipientId);

    /** Global manager feed for a specific manager (their personal copies of GLOBAL notifications). */
    List<NotificationEntity> findByRecipient_IdAndScopeOrderByCreatedAtDesc(Integer recipientId, String scope);

    /** Notifications created since a given id, used for client-side polling. */
    List<NotificationEntity> findByRecipient_IdAndIdGreaterThanOrderByCreatedAtDesc(Integer recipientId, Integer afterId);

    /**
     * De-duplication check: returns true if a reminder of the given type was
     * already sent for this package after the given timestamp.
     */
    boolean existsByRelatedPackage_IdAndTypeAndCreatedAtAfter(Integer packageId, String type, LocalDateTime since);

    /**
     * Counts how many reminders of the given type have been sent for a package.
     * Used to enforce the maxReminders limit.
     */
    int countByRelatedPackage_IdAndType(Integer packageId, String type);
}
