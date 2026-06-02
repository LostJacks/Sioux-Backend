package org.spms.spmsbackend.dtos;

public class StorePackageRequest {
    private String trackingNumber;
    /** Null for Sioux / unknown packages. */
    private Integer employeeId;
    private Integer rowId;
    /** "Sioux" for company packages, null for unknown/unaddressed. */
    private String recipientName;

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }

    public Integer getRowId() { return rowId; }
    public void setRowId(Integer rowId) { this.rowId = rowId; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
}
