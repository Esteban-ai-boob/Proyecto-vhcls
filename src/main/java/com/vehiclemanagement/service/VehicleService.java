package com.vehiclemanagement.service;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vehiclemanagement.dto.VehicleDTO;
import com.vehiclemanagement.dto.VehicleDocumentDTO;
import com.vehiclemanagement.entity.Document;
import com.vehiclemanagement.entity.Vehicle;
import com.vehiclemanagement.entity.VehicleDocument;
import com.vehiclemanagement.exception.ResourceNotFoundException;
import com.vehiclemanagement.exception.ValidationException;
import com.vehiclemanagement.repository.VehicleDocumentRepository;
import com.vehiclemanagement.repository.VehicleRepository;

/**
 * Servicio para la gestion de vehiculos.
 */
@Service
@Transactional
public class VehicleService implements IVehicleService {

    private final VehicleRepository vehicleRepository;
    private final IDocumentService documentService;
    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final EmailService emailService;

    public VehicleService(
        VehicleRepository vehicleRepository,
        IDocumentService documentService,
        VehicleDocumentRepository vehicleDocumentRepository,
        EmailService emailService
    ) {
        this.vehicleRepository = vehicleRepository;
        this.documentService = documentService;
        this.vehicleDocumentRepository = vehicleDocumentRepository;
        this.emailService = emailService;
    }

    @Override
    public Vehicle createVehicle(VehicleDTO vehicleDTO, List<Long> documentIds) {
        if (documentIds == null || documentIds.isEmpty()) {
            throw new ValidationException("Un vehiculo debe tener al menos un documento asociado");
        }

        String normalizedLicensePlate = vehicleDTO.getLicensePlate().trim().toUpperCase();
        if (vehicleRepository.existsByLicensePlate(normalizedLicensePlate)) {
            throw new ValidationException("Ya existe un vehiculo con la placa: " + normalizedLicensePlate);
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleType(vehicleDTO.getVehicleType());
        vehicle.setLicensePlate(normalizedLicensePlate);
        vehicle.setServiceType(vehicleDTO.getServiceType());
        vehicle.setFuelType(vehicleDTO.getFuelType());
        vehicle.setPassengerCapacity(vehicleDTO.getPassengerCapacity());
        vehicle.setColor(vehicleDTO.getColor());
        vehicle.setModelYear(vehicleDTO.getModelYear());
        vehicle.setBrand(vehicleDTO.getBrand());
        vehicle.setLine(vehicleDTO.getLine());

        vehicle.validateLicensePlate();
        vehicle = vehicleRepository.save(vehicle);

        LocalDate today = LocalDate.now();
        for (Long documentId : documentIds) {
            Document document = documentService.getDocumentById(documentId);

            if (!document.isApplicableFor(vehicle.getVehicleType())) {
                throw new ValidationException(
                    "El documento " + document.getDocumentName()
                        + " no es aplicable para tipo de vehiculo: "
                        + vehicle.getVehicleType().name()
                );
            }

            VehicleDocument vehicleDocument = new VehicleDocument();
            vehicleDocument.setVehicle(vehicle);
            vehicleDocument.setDocument(document);
            vehicleDocument.setIssuanceDate(today);
            vehicleDocument.setExpirationDate(today.plusYears(1));
            vehicleDocument.setDocumentStatus(VehicleDocument.DocumentStatus.EN_VERIFICACION);

            vehicleDocumentRepository.save(vehicleDocument);
            vehicle.getDocuments().add(vehicleDocument);
        }

        return getVehicleById(vehicle.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicle getVehicleById(long id) {
        return vehicleRepository.findByIdWithDocuments(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vehiculo no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAllWithDocuments();
    }

    @Override
    public Vehicle updateVehicle(long id, VehicleDTO vehicleDTO) {
        Vehicle vehicle = getVehicleById(id);
        String normalizedLicensePlate = vehicleDTO.getLicensePlate().trim().toUpperCase();

        if (!vehicle.getLicensePlate().equals(normalizedLicensePlate)
            && vehicleRepository.existsByLicensePlate(normalizedLicensePlate)) {
            throw new ValidationException("Ya existe un vehiculo con la placa: " + normalizedLicensePlate);
        }

        vehicle.setVehicleType(vehicleDTO.getVehicleType());
        vehicle.setLicensePlate(normalizedLicensePlate);
        vehicle.setServiceType(vehicleDTO.getServiceType());
        vehicle.setFuelType(vehicleDTO.getFuelType());
        vehicle.setPassengerCapacity(vehicleDTO.getPassengerCapacity());
        vehicle.setColor(vehicleDTO.getColor());
        vehicle.setModelYear(vehicleDTO.getModelYear());
        vehicle.setBrand(vehicleDTO.getBrand());
        vehicle.setLine(vehicleDTO.getLine());

        vehicle.validateLicensePlate();
        vehicleRepository.save(vehicle);
        return getVehicleById(id);
    }

    @Override
    public void deleteVehicle(long id) {
        getVehicleById(id);
        vehicleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicle getVehicleByLicensePlate(String licensePlate) {
        String normalizedLicensePlate = Objects.requireNonNull(
            licensePlate,
            "licensePlate no puede ser null"
        ).trim().toUpperCase();
        return vehicleRepository.findByLicensePlateWithDocuments(normalizedLicensePlate)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Vehiculo no encontrado con placa: " + normalizedLicensePlate
            ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByType(Vehicle.VehicleType vehicleType) {
        return vehicleRepository.findByVehicleTypeWithDocuments(vehicleType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByDocumentId(long documentId) {
        documentService.getDocumentById(documentId);
        return vehicleRepository.findVehiclesByDocumentId(documentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByDocumentStatus(VehicleDocument.DocumentStatus status) {
        return vehicleRepository.findVehiclesByDocumentStatus(
            Objects.requireNonNull(status, "status no puede ser null")
        );
    }

    @Override
    public VehicleDocument addDocumentToVehicle(long vehicleId, VehicleDocumentDTO vehicleDocumentDTO) {
        Vehicle vehicle = getVehicleById(vehicleId);
        long documentId = Objects.requireNonNull(
            vehicleDocumentDTO.getDocumentId(),
            "documentId no puede ser null"
        );
        Document document = documentService.getDocumentById(documentId);

        if (vehicleDocumentRepository.existsByVehicleIdAndDocumentId(vehicleId, documentId)) {
            throw new ValidationException("El documento ya esta asociado a este vehiculo");
        }

        if (!document.isApplicableFor(vehicle.getVehicleType())) {
            throw new ValidationException(
                "El documento " + document.getDocumentName()
                    + " no es aplicable para tipo de vehiculo: "
                    + vehicle.getVehicleType().name()
            );
        }

        VehicleDocument vehicleDocument = new VehicleDocument();
        vehicleDocument.setVehicle(vehicle);
        vehicleDocument.setDocument(document);
        vehicleDocument.setIssuanceDate(vehicleDocumentDTO.getIssuanceDate());
        vehicleDocument.setExpirationDate(vehicleDocumentDTO.getExpirationDate());
        vehicleDocument.setDocumentContent(vehicleDocumentDTO.getDocumentContent());
        if (vehicleDocumentDTO.getDocumentContent() != null && !vehicleDocumentDTO.getDocumentContent().isBlank()) {
            vehicleDocument.setDocumentBlob(Base64.getDecoder().decode(vehicleDocumentDTO.getDocumentContent()));
        }
        vehicleDocument.setDocumentStatus(
            vehicleDocumentDTO.getDocumentStatus() != null
                ? vehicleDocumentDTO.getDocumentStatus()
                : VehicleDocument.DocumentStatus.EN_VERIFICACION
        );

        vehicleDocument.validateDates();
        VehicleDocument savedDocument = vehicleDocumentRepository.save(vehicleDocument);

        // Enviar correo de notificacion si se proporciona un email
        if (vehicleDocumentDTO.getNotificationEmail() != null && 
            !vehicleDocumentDTO.getNotificationEmail().isEmpty()) {
            try {
                String notificationEmail = Objects.requireNonNull(
                    vehicleDocumentDTO.getNotificationEmail(),
                    "notificationEmail no puede ser null"
                );
                String licensePlate = Objects.requireNonNull(
                    vehicle.getLicensePlate(),
                    "licensePlate no puede ser null"
                );
                String documentName = Objects.requireNonNull(
                    document.getDocumentName(),
                    "documentName no puede ser null"
                );
                String documentCode = Objects.requireNonNull(
                    document.getDocumentCode(),
                    "documentCode no puede ser null"
                );

                emailService.sendDocumentUploadNotification(
                    notificationEmail,
                    licensePlate,
                    documentName,
                    documentCode,
                    vehicleDocument.getDocumentBlob()
                );
            } catch (Exception e) {
                // Log del error pero no fallar la operacion
                System.err.println("Advertencia: No se pudo enviar correo de notificacion: " + e.getMessage());
            }
        }

        return savedDocument;
    }

    @Override
    public void removeDocumentFromVehicle(long vehicleId, long vehicleDocumentId) {
        VehicleDocument vehicleDocument = vehicleDocumentRepository.findById(vehicleDocumentId)
            .orElseThrow(() -> new ResourceNotFoundException("Relacion vehiculo-documento no encontrada"));

        if (!vehicleDocument.getVehicle().getId().equals(vehicleId)) {
            throw new ValidationException("El documento no pertenece a este vehiculo");
        }

        long documentCount = vehicleDocumentRepository.countByVehicleId(vehicleId);
        if (documentCount <= 1) {
            throw new ValidationException("No se puede eliminar el ultimo documento del vehiculo");
        }

        vehicleDocumentRepository.deleteById(vehicleDocumentId);
    }

    @Override
    public VehicleDocument updateVehicleDocument(
        long vehicleId,
        long vehicleDocumentId,
        VehicleDocumentDTO vehicleDocumentDTO
    ) {
        VehicleDocument vehicleDocument = vehicleDocumentRepository.findById(vehicleDocumentId)
            .orElseThrow(() -> new ResourceNotFoundException("Relacion vehiculo-documento no encontrada"));

        if (!vehicleDocument.getVehicle().getId().equals(vehicleId)) {
            throw new ValidationException("El documento no pertenece a este vehiculo");
        }

        vehicleDocument.setIssuanceDate(vehicleDocumentDTO.getIssuanceDate());
        vehicleDocument.setExpirationDate(vehicleDocumentDTO.getExpirationDate());
        if (vehicleDocumentDTO.getDocumentStatus() != null) {
            vehicleDocument.setDocumentStatus(vehicleDocumentDTO.getDocumentStatus());
        }
        if (vehicleDocumentDTO.getDocumentContent() != null && !vehicleDocumentDTO.getDocumentContent().isBlank()) {
            vehicleDocument.setDocumentContent(vehicleDocumentDTO.getDocumentContent());
            vehicleDocument.setDocumentBlob(Base64.getDecoder().decode(vehicleDocumentDTO.getDocumentContent()));
        }

        vehicleDocument.validateDates();
        VehicleDocument savedDocument = vehicleDocumentRepository.save(vehicleDocument);

        // Enviar correo de notificacion si se proporciona un email
        if (vehicleDocumentDTO.getNotificationEmail() != null && 
            !vehicleDocumentDTO.getNotificationEmail().isEmpty()) {
            try {
                String notificationEmail = Objects.requireNonNull(
                    vehicleDocumentDTO.getNotificationEmail(),
                    "notificationEmail no puede ser null"
                );
                String licensePlate = Objects.requireNonNull(
                    vehicleDocument.getVehicle().getLicensePlate(),
                    "licensePlate no puede ser null"
                );
                String documentName = Objects.requireNonNull(
                    vehicleDocument.getDocument().getDocumentName(),
                    "documentName no puede ser null"
                );
                String documentCode = Objects.requireNonNull(
                    vehicleDocument.getDocument().getDocumentCode(),
                    "documentCode no puede ser null"
                );

                emailService.sendDocumentUploadNotification(
                    notificationEmail,
                    licensePlate,
                    documentName,
                    documentCode,
                    vehicleDocument.getDocumentBlob()
                );
            } catch (Exception e) {
                System.err.println("Advertencia: No se pudo enviar correo de notificacion en update: " + e.getMessage());
            }
        }

        return savedDocument;
    }
}
