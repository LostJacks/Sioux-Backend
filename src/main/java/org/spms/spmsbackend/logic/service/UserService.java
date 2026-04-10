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
}