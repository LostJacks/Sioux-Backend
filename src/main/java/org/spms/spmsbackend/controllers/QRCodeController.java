package org.spms.spmsbackend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.spms.spmsbackend.domain.users.User;
import org.spms.spmsbackend.dtos.EmployeeQRCodeDTO;
import org.spms.spmsbackend.logic.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/qrcodes")
@CrossOrigin(origins = "http://localhost:5173")
public class QRCodeController {

    private final UserService userService;

    public QRCodeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<EmployeeQRCodeDTO> getEmployeeQrCode(@PathVariable Integer employeeId) {
        try {
            User employee = userService.getUserById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

            EmployeeQRCodeDTO response = new EmployeeQRCodeDTO(
                    employee.getId(),
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getRole().name(),
                    null,
                    employee.getFirstName() + " " + employee.getLastName() + " - " + employee.getRole().name()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeQRCodeDTO>> getAllEmployeesWithQrCodes() {
        try {
            List<EmployeeQRCodeDTO> qrCodeDataList = userService.getAllEmployees()
                    .stream()
                    .map(employee -> new EmployeeQRCodeDTO(
                            employee.getId(),
                            employee.getFirstName(),
                            employee.getLastName(),
                            employee.getRole().name(),
                            null,
                            employee.getFirstName() + " " + employee.getLastName() + " - " + employee.getRole().name()
                    ))
                    .toList();
            return ResponseEntity.ok(qrCodeDataList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}