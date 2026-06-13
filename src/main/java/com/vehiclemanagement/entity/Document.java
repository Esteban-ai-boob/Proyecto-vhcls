package com.vehiclemanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Check;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad parametrica que representa tipos de documentos que pueden estar asociados a vehiculos.
 */
@Entity
@Check(constraints = "vehicle_type_applicability in ('A', 'M', 'AM', 'AUTOMOVIL', 'MOTOCICLETA', 'AMBOS') and mandatory_flag in ('RA', 'RM', 'RR', '0', '1')")
@Table(name = "documents", uniqueConstraints = {
    @UniqueConstraint(columnNames = "document_code", name = "uk_documentos_codigo")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "El codigo del documento es requerido")
    @Column(name = "document_code", unique = true, length = 20)
    private String documentCode;

    @NotBlank(message = "El nombre del documento es requerido")
    @Column(name = "document_name", nullable = false)
    private String documentName;

    @NotNull(message = "El tipo de vehiculo para el cual aplica es requerido")
    @Column(name = "vehicle_type_applicability", nullable = false, length = 20)
    private String vehicleTypeApplicability;

    @NotNull(message = "El caracter obligatorio es requerido")
    @Column(name = "mandatory_flag", nullable = false, length = 2)
    private String mandatoryFlag;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<VehicleDocument> vehicleDocuments = new HashSet<>();

    public Document() {
    }

    public Document(
        Long id,
        String documentCode,
        String documentName,
        String vehicleTypeApplicability,
        Boolean mandatory,
        String description,
        Set<VehicleDocument> vehicleDocuments
    ) {
        this.id = id;
        this.documentCode = documentCode;
        this.documentName = documentName;
        this.vehicleTypeApplicability = vehicleTypeApplicability;
        setMandatory(mandatory);
        this.description = description;
        this.vehicleDocuments = vehicleDocuments != null ? vehicleDocuments : new HashSet<>();
    }

    @PrePersist
    @PreUpdate
    public void validateParametrizedFields() {
        if (vehicleTypeApplicability != null) {
            String applicability = normalizeApplicability(vehicleTypeApplicability);
            if (!applicability.matches("^(A|M|AM|AUTOMOVIL|MOTOCICLETA|AMBOS)$")) {
                throw new IllegalArgumentException(
                    "La aplicabilidad de tipo de vehiculo debe ser: A, M, AM, AUTOMOVIL, MOTOCICLETA o AMBOS"
                );
            }
            this.vehicleTypeApplicability = applicability;
        }
        if (mandatoryFlag == null || mandatoryFlag.isBlank()) {
            mandatoryFlag = "RR";
        }
        mandatoryFlag = normalizeMandatoryFlag(mandatoryFlag);
    }

    public boolean isApplicableFor(Vehicle.VehicleType vehicleType) {
        if ("AM".equals(vehicleTypeApplicability) || "AMBOS".equals(vehicleTypeApplicability)) {
            return true;
        }
        if (("A".equals(vehicleTypeApplicability) || "AUTOMOVIL".equals(vehicleTypeApplicability))
            && vehicleType == Vehicle.VehicleType.AUTOMOVIL) {
            return true;
        }
        return ("M".equals(vehicleTypeApplicability) || "MOTOCICLETA".equals(vehicleTypeApplicability))
            && vehicleType == Vehicle.VehicleType.MOTOCICLETA;
    }

    public boolean isMandatoryFor(Vehicle.VehicleType vehicleType) {
        return Boolean.TRUE.equals(getMandatory()) && isApplicableFor(vehicleType);
    }

    public static String normalizeApplicability(String value) {
        String normalized = value.toUpperCase().trim();
        if ("AUTOMOVIL".equals(normalized)) {
            return "A";
        }
        if ("MOTOCICLETA".equals(normalized)) {
            return "M";
        }
        if ("AMBOS".equals(normalized)) {
            return "AM";
        }
        return normalized;
    }

    private static String normalizeMandatoryFlag(String value) {
        String normalized = value.toUpperCase().trim();
        if ("1".equals(normalized) || "TRUE".equals(normalized) || "RM".equals(normalized)) {
            return "RM";
        }
        if ("0".equals(normalized) || "FALSE".equals(normalized) || "RR".equals(normalized)) {
            return "RR";
        }
        return normalized;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return "RM".equalsIgnoreCase(mandatoryFlag) || "RA".equalsIgnoreCase(mandatoryFlag);
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatoryFlag = Boolean.TRUE.equals(mandatory) ? "RM" : "RR";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<VehicleDocument> getVehicleDocuments() {
        return vehicleDocuments;
    }

    public void setVehicleDocuments(Set<VehicleDocument> vehicleDocuments) {
        this.vehicleDocuments = vehicleDocuments;
    }
}
