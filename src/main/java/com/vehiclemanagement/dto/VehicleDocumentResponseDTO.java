package com.vehiclemanagement.dto;

import com.vehiclemanagement.entity.VehicleDocument;

import java.time.LocalDate;

/**
 * DTO para presentacion de documentos de vehiculos en respuestas.
 */
public class VehicleDocumentResponseDTO {

    private Long id;
    private Long documentId;
    private String documentCode;
    private String documentName;
    private LocalDate issuanceDate;
    private LocalDate expirationDate;
    private VehicleDocument.DocumentStatus documentStatus;

    public VehicleDocumentResponseDTO() {
    }

    public VehicleDocumentResponseDTO(
        Long id,
        Long documentId,
        String documentCode,
        String documentName,
        LocalDate issuanceDate,
        LocalDate expirationDate,
        VehicleDocument.DocumentStatus documentStatus
    ) {
        this.id = id;
        this.documentId = documentId;
        this.documentCode = documentCode;
        this.documentName = documentName;
        this.issuanceDate = issuanceDate;
        this.expirationDate = expirationDate;
        this.documentStatus = documentStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public void setDocumentCode(String documentCode) {
        this.documentCode = documentCode;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public LocalDate getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(LocalDate issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public VehicleDocument.DocumentStatus getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(VehicleDocument.DocumentStatus documentStatus) {
        this.documentStatus = documentStatus;
    }
}
