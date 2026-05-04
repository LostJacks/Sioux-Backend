package org.spms.spmsbackend.controllers;

import org.spms.spmsbackend.domain.users.User;
import org.spms.spmsbackend.logic.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Lightweight user lookup endpoints. US-03 only needs the manager picker;
 * if {@code role} is omitted the endpoint behaves like a generic "list users".
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
}
