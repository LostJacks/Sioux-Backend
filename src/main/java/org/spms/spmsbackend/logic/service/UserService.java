package org.spms.spmsbackend.logic.service;

import org.spms.spmsbackend.domain.users.User;
import org.spms.spmsbackend.mapper.UserMapper;
import org.spms.spmsbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<User> getAllEmployees() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDomain)
                .toList();
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id)
                .map(userMapper::toDomain);
    }

    /** Used by US-03 receptionist UI to populate the manager picker. */
    public List<User> getUsersByRole(String role) {
        if (role == null || role.isBlank()) {
            return getAllEmployees();
        }
        return userRepository.findByRoleIgnoreCase(role.trim())
                .stream()
                .map(userMapper::toDomain)
                .toList();
    }
}
