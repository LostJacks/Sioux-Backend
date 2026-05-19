package org.spms.spmsbackend.repository;

import org.spms.spmsbackend.entity.ReminderSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderSettingsRepository extends JpaRepository<ReminderSettingsEntity, Integer> {
}
