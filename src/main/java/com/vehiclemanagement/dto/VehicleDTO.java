package com.vehiclemanagement.dto;

import com.vehiclemanagement.entity.Vehicle;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para creacion y actualizacion de vehiculos.
 */
public class VehicleDTO {

    @NotNull(message = "El tipo de vehiculo es requerido")
    private Vehicle.VehicleType vehicleType;

    @NotBlank(message = "La placa es requerida")
    private String licensePlate;

    @NotNull(message = "El tipo de servicio es requerido")
    private Vehicle.ServiceType serviceType;

    @NotNull(message = "El tipo de combustible es requerido")
    private Vehicle.FuelType fuelType;

    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    private Integer passengerCapacity;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "El color debe ser un codigo hexadecimal valido (#RRGGBB)")
    private String color;

    @Min(value = 1900, message = "El modelo debe ser un ano valido")
    private Integer modelYear;

    private String brand;

    private String line;

    public VehicleDTO() {
    }

    public VehicleDTO(
        Vehicle.VehicleType vehicleType,
        String licensePlate,
        Vehicle.ServiceType serviceType,
        Vehicle.FuelType fuelType,
        Integer passengerCapacity,
        String color,
        Integer modelYear,
        String brand,
        String line
    ) {
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.serviceType = serviceType;
        this.fuelType = fuelType;
        this.passengerCapacity = passengerCapacity;
        this.color = color;
        this.modelYear = modelYear;
        this.brand = brand;
        this.line = line;
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
}
