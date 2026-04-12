package org.spms.spmsbackend.domain;

public class Employee {

    private Long id;
    private String name;
    private String barcode;

    public Employee() {}

    public Employee(Long id, String name, String barcode) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
    }

    // Getters și Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}


