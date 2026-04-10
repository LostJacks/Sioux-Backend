package org.spms.spmsbackend.dtos;

public class EmployeeQRCodeDTO {
    private Integer employeeId;
    private String firstName;
    private String lastName;
    private String role;
    private String qrCode;
    private String label;

    public EmployeeQRCodeDTO() {}

    public EmployeeQRCodeDTO(Integer employeeId, String firstName, String lastName, String role, String qrCode, String label) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.qrCode = qrCode;
        this.label = label;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

