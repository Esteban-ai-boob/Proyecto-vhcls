package com.vehiclemanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Check;

import java.time.LocalDate;

/**
 * Entidad que representa la relacion entre un vehiculo y sus documentos asociados.
 */
@Entity
@Check(constraints = "Estado in ('En Verificación', 'Habilitado', 'Vendido/Vencido')")
@Table(name = "vehiculo_documento",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"vehiculo_id", "documento_id"}, name = "uk_vehiculo_documento")
    }
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VehicleDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "El vehiculo es requerido")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehicle vehicle;

    @NotNull(message = "El documento es requerido")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "documento_id", nullable = false)
    private Document document;

    @NotNull(message = "La fecha de expedicion es requerida")
    @Column(name = "fecha_expedicion", nullable = false)
    private LocalDate issuanceDate;

    @NotNull(message = "La fecha de vencimiento es requerida")
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate expirationDate;

    @NotNull(message = "El estado del documento es requerido")
    @Column(name = "estado", nullable = false)
    @Convert(converter = DocumentStatusConverter.class)
    private DocumentStatus documentStatus;

    @Column(name = "contenido_documento", columnDefinition = "LONGTEXT")
    private String documentContent;

    @Lob
    @JsonIgnore
    @Column(name = "archivo_documento", columnDefinition = "LONGBLOB")
    private byte[] documentBlob;

    public VehicleDocument() {
    }

    public VehicleDocument(
        Long id,
        Vehicle vehicle,
        Document document,
        LocalDate issuanceDate,
        LocalDate expirationDate,
        DocumentStatus documentStatus
    ) {
        this.id = id;
        this.vehicle = vehicle;
        this.document = document;
        this.issuanceDate = issuanceDate;
        this.expirationDate = expirationDate;
        this.documentStatus = documentStatus;
        this.documentContent = null;
    }

    @PrePersist
    @PreUpdate
    public void validateDates() {
        if (issuanceDate != null && expirationDate != null && issuanceDate.isAfter(expirationDate)) {
            throw new IllegalArgumentException(
                "La fecha de expedicion debe ser anterior a la fecha de vencimiento"
            );
        }
    }

    @PostLoad
    public void updateStatusBasedOnDate() {
        if (expirationDate != null && documentStatus != DocumentStatus.EN_VERIFICACION) {
            LocalDate today = LocalDate.now();
            if (today.isAfter(expirationDate)) {
                this.documentStatus = DocumentStatus.VENDIDO_VENCIDO;
            } else if (documentStatus == DocumentStatus.VENDIDO_VENCIDO) {
                this.documentStatus = DocumentStatus.HABILITADO;
            }
        }
    }

    public enum DocumentStatus {
        HABILITADO("Habilitado"),
        VENDIDO_VENCIDO("Vendido/Vencido"),
        EN_VERIFICACION("En Verificación");

        private final String description;

        DocumentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public static DocumentStatus fromText(String value) {
            String normalized = value.trim();
            for (DocumentStatus status : values()) {
                if (status.name().equalsIgnoreCase(normalized)
                    || status.description.equalsIgnoreCase(normalized)
                    || ("HABILITADOS".equalsIgnoreCase(normalized) && status == HABILITADO)
                    || ("VENCIDOS".equalsIgnoreCase(normalized) && status == VENDIDO_VENCIDO)
                    || ("VENCIDO".equalsIgnoreCase(normalized) && status == VENDIDO_VENCIDO)
                    || ("VENDIDO/VENCIDO".equalsIgnoreCase(normalized) && status == VENDIDO_VENCIDO)) {
                    return status;
                }
            }
            throw new IllegalArgumentException(
                "Estado invalido. Use Habilitado, Vendido/Vencido o En Verificación"
            );
        }
    }

    @Converter(autoApply = false)
    public static class DocumentStatusConverter implements AttributeConverter<DocumentStatus, String> {

        @Override
        public String convertToDatabaseColumn(DocumentStatus attribute) {
            return attribute == null ? null : attribute.getDescription();
        }

        @Override
        public DocumentStatus convertToEntityAttribute(String dbData) {
            return dbData == null ? null : DocumentStatus.fromText(dbData);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
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

    public DocumentStatus getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(DocumentStatus documentStatus) {
        this.documentStatus = documentStatus;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }

    public byte[] getDocumentBlob() {
        return documentBlob;
    }

    public void setDocumentBlob(byte[] documentBlob) {
        this.documentBlob = documentBlob;
    }
}
