package com.vehiclemanagement.service;

import com.vehiclemanagement.dto.VehicleDTO;
import com.vehiclemanagement.dto.VehicleDocumentDTO;
import com.vehiclemanagement.entity.Vehicle;
import com.vehiclemanagement.entity.VehicleDocument;

import java.util.List;

public interface IVehicleService {

    Vehicle createVehicle(VehicleDTO vehicleDTO, List<Long> documentIds);

    Vehicle getVehicleById(long id);

    List<Vehicle> getAllVehicles();

    Vehicle updateVehicle(long id, VehicleDTO vehicleDTO);

    void deleteVehicle(long id);

    Vehicle getVehicleByLicensePlate(String licensePlate);

    List<Vehicle> getVehiclesByType(Vehicle.VehicleType vehicleType);

    List<Vehicle> getVehiclesByDocumentId(long documentId);

    List<Vehicle> getVehiclesByDocumentStatus(VehicleDocument.DocumentStatus status);

    VehicleDocument addDocumentToVehicle(long vehicleId, VehicleDocumentDTO vehicleDocumentDTO);

    void removeDocumentFromVehicle(long vehicleId, long vehicleDocumentId);

    VehicleDocument updateVehicleDocument(long vehicleId, long vehicleDocumentId, VehicleDocumentDTO vehicleDocumentDTO);
}
