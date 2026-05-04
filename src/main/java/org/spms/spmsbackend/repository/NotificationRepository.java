package org.spms.spmsbackend.repository;

import org.spms.spmsbackend.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Integer> {

    /** Personal notification feed for an employee, newest first. */
    List<NotificationEntity> findByRecipient_IdOrderByCreatedAtDesc(Integer recipientId);

    /** Global manager feed for a specific manager (their personal copies of GLOBAL notifications). */
    List<NotificationEntity> findByRecipient_IdAndScopeOrderByCreatedAtDesc(Integer recipientId, String scope);

    /** Notifications created since a given id, used for client-side polling. */
    List<NotificationEntity> findByRecipient_IdAndIdGreaterThanOrderByCreatedAtDesc(Integer recipientId, Integer afterId);
}
