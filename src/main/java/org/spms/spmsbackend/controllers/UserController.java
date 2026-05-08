package org.spms.spmsbackend.controllers;

import org.spms.spmsbackend.domain.users.User;
import org.spms.spmsbackend.dtos.CreateUserRequest;
import org.spms.spmsbackend.logic.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User lookup + creation endpoints.
 *
 * <p>{@code GET /api/users} lists users (optionally filtered by role — used
 * by the US-03 receptionist UI to populate the manager picker).
 * {@code POST /api/users} creates a new user (added on develop).</p>
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers(@RequestParam(name = "role", required = false) String role) {
        if (role == null || role.isBlank()) {
            return ResponseEntity.ok(userService.getAllEmployees());
        }
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }
}
