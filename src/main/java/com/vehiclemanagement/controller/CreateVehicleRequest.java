package com.vehiclemanagement.controller;

import com.vehiclemanagement.dto.VehicleDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Request DTO para crear un vehículo con documentos asociados.
 */
public class CreateVehicleRequest {

    @Valid
    @NotNull(message = "La informacion del vehiculo es requerida")
    private VehicleDTO vehicle;

    @NotEmpty(message = "Debe enviar al menos un documento asociado")
    private List<Long> documentIds;

    public CreateVehicleRequest() {
    }

    public CreateVehicleRequest(VehicleDTO vehicle, List<Long> documentIds) {
        this.vehicle = vehicle;
        this.documentIds = documentIds;
    }

    public VehicleDTO getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleDTO vehicle) {
        this.vehicle = vehicle;
    }

    public List<Long> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<Long> documentIds) {
        this.documentIds = documentIds;
    }
}
