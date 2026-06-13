package com.vehiclemanagement.dto;

import com.vehiclemanagement.entity.VehicleDocument;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;

import java.time.LocalDate;

/**
 * DTO para creacion y actualizacion de documentos de vehiculos.
 */
public class VehicleDocumentDTO {

    @NotNull(message = "El ID del documento es requerido")
    private Long documentId;

    @NotNull(message = "La fecha de expedicion es requerida")
    private LocalDate issuanceDate;

    @NotNull(message = "La fecha de vencimiento es requerida")
    private LocalDate expirationDate;

    private VehicleDocument.DocumentStatus documentStatus;

    private String documentContent;

    @Email(message = "El correo de notificacion debe ser valido")
    private String notificationEmail;

    public VehicleDocumentDTO() {
    }

    public VehicleDocumentDTO(
        Long documentId,
        LocalDate issuanceDate,
        LocalDate expirationDate,
        VehicleDocument.DocumentStatus documentStatus
    ) {
        this.documentId = documentId;
        this.issuanceDate = issuanceDate;
        this.expirationDate = expirationDate;
        this.documentStatus = documentStatus;
        this.documentContent = null;
        this.notificationEmail = null;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
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

    public String getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }
}
