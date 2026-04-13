package org.spms.spmsbackend.dtos;

public class QRCodeDataDTO {
    private Integer rowId;
    private Integer rowNumber;
    private String qrCode;
    private String label;

    public QRCodeDataDTO() {}

    public QRCodeDataDTO(Integer rowId, Integer rowNumber, String qrCode, String label) {
        this.rowId = rowId;
        this.rowNumber = rowNumber;
        this.qrCode = qrCode;
        this.label = label;
    }

    public Integer getRowId() {
        return rowId;
    }

    public void setRowId(Integer rowId) {
        this.rowId = rowId;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
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

