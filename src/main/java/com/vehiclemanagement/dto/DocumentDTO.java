package com.vehiclemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para creacion y actualizacion de documentos parametrizados.
 */
public class DocumentDTO {

    private String documentCode;

    @NotBlank(message = "El nombre del documento es requerido")
    private String documentName;

    @NotNull(message = "El tipo de vehiculo para el cual aplica es requerido")
    @Pattern(regexp = "^(A|M|AM|AUTOMOVIL|MOTOCICLETA|AMBOS)$", message = "Debe ser AUTOMOVIL, MOTOCICLETA o AMBOS")
    private String vehicleTypeApplicability;

    @NotNull(message = "El caracter obligatorio es requerido")
    private Boolean mandatory;

    private String description;

    public DocumentDTO() {
    }

    public DocumentDTO(
        String documentCode,
        String documentName,
        String vehicleTypeApplicability,
        Boolean mandatory,
        String description
    ) {
        this.documentCode = documentCode;
        this.documentName = documentName;
        this.vehicleTypeApplicability = vehicleTypeApplicability;
        this.mandatory = mandatory;
        this.description = description;
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

    public String getVehicleTypeApplicability() {
        return vehicleTypeApplicability;
    }

    public void setVehicleTypeApplicability(String vehicleTypeApplicability) {
        this.vehicleTypeApplicability = vehicleTypeApplicability;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
