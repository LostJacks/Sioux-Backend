package org.spms.spmsbackend.repository;

import org.spms.spmsbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    /** Case-insensitive role lookup; the entity stores role as a free-string. */
    List<UserEntity> findByRoleIgnoreCase(String role);
}
