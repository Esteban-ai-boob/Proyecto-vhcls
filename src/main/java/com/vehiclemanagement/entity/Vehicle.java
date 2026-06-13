package com.vehiclemanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.Check;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa un vehiculo en el sistema.
 */
@Entity
@Check(constraints = "vehicle_type in ('AUTOMOVIL', 'MOTOCICLETA') and service_type in ('PUBLICO', 'PRIVADO') and fuel_type in ('GASOLINA', 'GAS', 'DIESEL')")
@Table(name = "vehicles", uniqueConstraints = {
    @UniqueConstraint(columnNames = "license_plate", name = "uk_vehiculos_placa")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "El tipo de vehiculo es requerido")
    @Column(name = "vehicle_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @NotBlank(message = "La placa es requerida")
    @Column(name = "license_plate", nullable = false, unique = true, length = 6)
    private String licensePlate;

    @NotNull(message = "El tipo de servicio es requerido")
    @Column(name = "service_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @NotNull(message = "El tipo de combustible es requerido")
    @Column(name = "fuel_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @NotNull(message = "La capacidad de pasajeros es requerida")
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    @Column(name = "passenger_capacity")
    private Integer passengerCapacity;

    @NotBlank(message = "El color es requerido")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "El color debe ser un codigo hexadecimal valido (#RRGGBB)")
    @Column(name = "color")
    private String color;

    @NotNull(message = "El modelo es requerido")
    @Min(value = 1900, message = "El modelo debe ser un ano valido")
    @Column(name = "model_year")
    private Integer modelYear;

    @NotBlank(message = "La marca es requerida")
    @Column(name = "brand")
    private String brand;

    @NotBlank(message = "La linea es requerida")
    @Column(name = "line")
    private String line;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<VehicleDocument> documents = new HashSet<>();

    public Vehicle() {
    }

    public Vehicle(
        Long id,
        VehicleType vehicleType,
        String licensePlate,
        ServiceType serviceType,
        FuelType fuelType,
        Integer passengerCapacity,
        String color,
        Integer modelYear,
        String brand,
        String line,
        Set<VehicleDocument> documents
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
        this.documents = documents != null ? documents : new HashSet<>();
    }

    @PrePersist
    @PreUpdate
    public void validateLicensePlate() {
        if (licensePlate == null || vehicleType == null) {
            return;
        }

        String plate = licensePlate.toUpperCase().trim();

        if (vehicleType == VehicleType.AUTOMOVIL) {
            if (!plate.matches("^[A-Z]{3}[0-9]{3}$")) {
                throw new IllegalArgumentException(
                    "Placa de automovil invalida. Debe contener 3 letras seguidas de 3 numeros (ej: ABC123)"
                );
            }
        } else if (vehicleType == VehicleType.MOTOCICLETA) {
            if (!plate.matches("^[A-Z]{3}[0-9]{2}[A-Z]$")) {
                throw new IllegalArgumentException(
                    "Placa de motocicleta invalida. Debe contener 3 letras, 2 numeros y 1 letra (ej: ABC12D)"
                );
            }
        }

        this.licensePlate = plate;
    }

    public enum VehicleType {
        AUTOMOVIL("Automovil"),
        MOTOCICLETA("Motocicleta");

        private final String description;

        VehicleType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum ServiceType {
        PUBLICO("Publico", "Pu"),
        PRIVADO("Privado", "Pr");

        private final String description;
        private final String code;

        ServiceType(String description, String code) {
            this.description = description;
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public String getCode() {
            return code;
        }
    }

    public enum FuelType {
        GASOLINA("Gasolina"),
        GAS("Gas"),
        DIESEL("Diesel");

        private final String description;

        FuelType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
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

    public Set<VehicleDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<VehicleDocument> documents) {
        this.documents = documents;
    }
}
