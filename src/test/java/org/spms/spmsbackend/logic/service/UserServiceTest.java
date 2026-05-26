package org.spms.spmsbackend.logic.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spms.spmsbackend.domain.users.Role;
import org.spms.spmsbackend.domain.users.User;
import org.spms.spmsbackend.dtos.CreateUserRequest;
import org.spms.spmsbackend.entity.UserEntity;
import org.spms.spmsbackend.mapper.UserMapper;
import org.spms.spmsbackend.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserEntity sampleEntity;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleEntity = new UserEntity();
        sampleEntity.setId(1);
        sampleEntity.setFirstName("Alice");
        sampleEntity.setLastName("Smith");
        sampleEntity.setEmail("alice@sioux.eu");
        sampleEntity.setRole("EMPLOYEE");

        sampleUser = new User();
        sampleUser.setId(1);
        sampleUser.setFirstName("Alice");
        sampleUser.setLastName("Smith");
        sampleUser.setRole(Role.EMPLOYEE);
    }

    // ── getAllEmployees ──────────────────────────────────────────────────────

    @Test
    void getAllEmployees_returnsMappedList() {
        when(userRepository.findAll()).thenReturn(List.of(sampleEntity));
        when(userMapper.toDomain(sampleEntity)).thenReturn(sampleUser);

        List<User> result = userService.getAllEmployees();

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getFirstName());
    }

    @Test
    void getAllEmployees_emptyRepository_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> result = userService.getAllEmployees();

        assertTrue(result.isEmpty());
    }

    // ── getUserById ──────────────────────────────────────────────────────────

    @Test
    void getUserById_found_returnsMappedUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(sampleEntity));
        when(userMapper.toDomain(sampleEntity)).thenReturn(sampleUser);

        Optional<User> result = userService.getUserById(1);

        assertTrue(result.isPresent());
        assertEquals(sampleUser, result.get());
    }

    @Test
    void getUserById_notFound_returnsEmpty() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(99);

        assertTrue(result.isEmpty());
    }

    // ── getUsersByRole ───────────────────────────────────────────────────────

    @Test
    void getUsersByRole_withRole_queriesRepository() {
        when(userRepository.findByRoleIgnoreCase("MANAGER"))
                .thenReturn(List.of(sampleEntity));
        when(userMapper.toDomain(sampleEntity)).thenReturn(sampleUser);

        List<User> result = userService.getUsersByRole("MANAGER");

        assertEquals(1, result.size());
        verify(userRepository).findByRoleIgnoreCase("MANAGER");
        verify(userRepository, never()).findAll();
    }

    @Test
    void getUsersByRole_nullRole_fallsBackToGetAll() {
        when(userRepository.findAll()).thenReturn(List.of(sampleEntity));
        when(userMapper.toDomain(sampleEntity)).thenReturn(sampleUser);

        List<User> result = userService.getUsersByRole(null);

        assertEquals(1, result.size());
        verify(userRepository).findAll();
        verify(userRepository, never()).findByRoleIgnoreCase(anyString());
    }

    @Test
    void getUsersByRole_blankRole_fallsBackToGetAll() {
        when(userRepository.findAll()).thenReturn(List.of(sampleEntity));
        when(userMapper.toDomain(sampleEntity)).thenReturn(sampleUser);

        List<User> result = userService.getUsersByRole("   ");

        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    // ── createUser ───────────────────────────────────────────────────────────

    @Test
    void createUser_persistsEntityAndReturnsMappedUser() {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("Bob");
        request.setLastName("Jones");
        request.setEmail("bob@sioux.eu");
        request.setPassword("secret");
        request.setRole("MANAGER");

        when(userRepository.save(any(UserEntity.class))).thenReturn(sampleEntity);
        when(userMapper.toDomain(sampleEntity)).thenReturn(sampleUser);

        User result = userService.createUser(request);

        assertNotNull(result);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void createUser_nullRole_defaultsToEmployee() {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("Carol");
        request.setLastName("Lee");
        request.setEmail("carol@sioux.eu");
        request.setPassword("pass");
        request.setRole(null); // no role provided

        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity saved = inv.getArgument(0);
            // Default role must be EMPLOYEE
            assertEquals("EMPLOYEE", saved.getRole());
            return sampleEntity;
        });
        when(userMapper.toDomain(sampleEntity)).thenReturn(sampleUser);

        userService.createUser(request);

        verify(userRepository).save(any(UserEntity.class));
    }
}
