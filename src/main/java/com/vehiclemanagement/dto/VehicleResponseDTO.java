package com.vehiclemanagement.dto;

import com.vehiclemanagement.entity.Vehicle;

import java.util.Set;

/**
 * DTO para presentacion de vehiculos en respuestas.
 */
public class VehicleResponseDTO {

    private Long id;
    private Vehicle.VehicleType vehicleType;
    private String licensePlate;
    private Vehicle.ServiceType serviceType;
    private Vehicle.FuelType fuelType;
    private Integer passengerCapacity;
    private String color;
    private Integer modelYear;
    private String brand;
    private String line;
    private Set<VehicleDocumentResponseDTO> documents;

    public VehicleResponseDTO() {
    }

    public VehicleResponseDTO(
        Long id,
        Vehicle.VehicleType vehicleType,
        String licensePlate,
        Vehicle.ServiceType serviceType,
        Vehicle.FuelType fuelType,
        Integer passengerCapacity,
        String color,
        Integer modelYear,
        String brand,
        String line,
        Set<VehicleDocumentResponseDTO> documents
    ) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.serviceType = serviceType;
        this.fuelType = fuelType;
        this.passengerCapacity = passengerCapacity;
        this.color = color;
        this.modelYear = modelYear;
        this.brand = brand;
        this.line = line;
        this.documents = documents;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehicle.VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(Vehicle.VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Vehicle.ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(Vehicle.ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public Vehicle.FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(Vehicle.FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public Integer getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setPassengerCapacity(Integer passengerCapacity) {
        this.passengerCapacity = passengerCapacity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getModelYear() {
        return modelYear;
    }

    public void setModelYear(Integer modelYear) {
        this.modelYear = modelYear;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public Set<VehicleDocumentResponseDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<VehicleDocumentResponseDTO> documents) {
        this.documents = documents;
    }
}
