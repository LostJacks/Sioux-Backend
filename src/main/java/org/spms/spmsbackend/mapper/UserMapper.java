package org.spms.spmsbackend.mapper;

import org.spms.spmsbackend.domain.users.Role;
import org.spms.spmsbackend.domain.users.User;
import org.spms.spmsbackend.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setEmail(entity.getEmail());
        user.setRole(parseRole(entity.getRole()));
        user.setBuilding(
                entity.getBuilding() != null ? entity.getBuilding().getName() : null
        );
        return user;
    }

    private Role parseRole(String raw) {
        if (raw == null || raw.isBlank()) {
            return Role.EMPLOYEE;
        }
        try {
            return Role.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            // Unknown role string — don't blow up the response; treat as EMPLOYEE.
            return Role.EMPLOYEE;
        }
    }
}
