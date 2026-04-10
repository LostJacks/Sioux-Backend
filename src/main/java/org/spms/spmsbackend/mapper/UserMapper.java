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
        user.setRole(Role.valueOf(entity.getRole().toUpperCase()));
        user.setBuilding(
                entity.getBuilding() != null ? entity.getBuilding().getName() : null
        );
        return user;
    }
}