package com.vehiclemanagement.controller;

import com.vehiclemanagement.dto.VehicleDTO;
import com.vehiclemanagement.dto.VehicleDocumentDTO;
import com.vehiclemanagement.dto.VehicleDocumentResponseDTO;
import com.vehiclemanagement.dto.VehicleResponseDTO;
import com.vehiclemanagement.entity.Vehicle;
import com.vehiclemanagement.entity.VehicleDocument;
import com.vehiclemanagement.service.IVehicleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Controlador REST para la gestion de vehiculos.
 */
@Tag(name = "2. Gestión de Vehículos", description = "Operaciones CRUD para Vehículos")
@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final IVehicleService vehicleService;

    public VehicleController(IVehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * POST /vehicles - Crea un nuevo vehiculo con documentos asociados.
     */
    @PostMapping
    public ResponseEntity<VehicleResponseDTO> createVehicle(
            @Valid @RequestBody CreateVehicleRequest request) {
        Vehicle createdVehicle = vehicleService.createVehicle(request.getVehicle(), request.getDocumentIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDTO(createdVehicle));
    }

    /**
     * GET /vehicles - Obtiene todos los vehiculos.
     */
    @GetMapping
    public ResponseEntity<List<VehicleResponseDTO>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        List<VehicleResponseDTO> response = vehicles.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /vehicles/{id} - Obtiene un vehiculo por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getVehicleById(@PathVariable long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(mapToResponseDTO(vehicle));
    }

    /**
     * GET /vehicles/plate/{licensePlate} - Busca vehiculo por numero de placa.
     */
    @GetMapping("/plate/{licensePlate}")
    public ResponseEntity<VehicleResponseDTO> getVehicleByLicensePlate(@PathVariable String licensePlate) {
        Vehicle vehicle = vehicleService.getVehicleByLicensePlate(licensePlate);
        return ResponseEntity.ok(mapToResponseDTO(vehicle));
    }

    /**
     * GET /vehicles/type/{type} - Busca vehiculos por tipo.
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<VehicleResponseDTO>> getVehiclesByType(@PathVariable Vehicle.VehicleType type) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByType(type);
        List<VehicleResponseDTO> response = vehicles.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /vehicles/document/{documentId} - Busca vehiculos que tengan un documento especifico.
     */
    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<VehicleResponseDTO>> getVehiclesByDocumentId(@PathVariable long documentId) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByDocumentId(documentId);
        List<VehicleResponseDTO> response = vehicles.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /vehicles/status/{status} - Busca vehiculos segun el estado del documento.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<VehicleResponseDTO>> getVehiclesByDocumentStatus(
            @PathVariable String status) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByDocumentStatus(
            VehicleDocument.DocumentStatus.fromText(status)
        );
        List<VehicleResponseDTO> response = vehicles.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /vehicles/{id} - Actualiza un vehiculo existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> updateVehicle(
            @PathVariable long id,
            @Valid @RequestBody VehicleDTO vehicleDTO) {
        Vehicle updatedVehicle = vehicleService.updateVehicle(id, vehicleDTO);
        return ResponseEntity.ok(mapToResponseDTO(updatedVehicle));
    }

    /**
     * DELETE /vehicles/{id} - Elimina un vehiculo por su ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /vehicles/{vehicleId}/documents - Agrega un documento a un vehiculo.
     */
    @PostMapping("/{vehicleId}/documents")
    public ResponseEntity<VehicleDocumentResponseDTO> addDocumentToVehicle(
            @PathVariable long vehicleId,
            @Valid @RequestBody VehicleDocumentDTO vehicleDocumentDTO) {
        VehicleDocument vehicleDocument = vehicleService.addDocumentToVehicle(vehicleId, vehicleDocumentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapDocumentToResponseDTO(vehicleDocument));
    }

    @PostMapping("/{vehicleId}/documents/batch-base64")
    public ResponseEntity<Map<String, Object>> uploadVehicleDocumentsBase64(
            @PathVariable long vehicleId,
            @RequestBody Object documentsPayload) {
        int updated = 0;
        for (Map<String, Object> item : normalizeDocumentsPayload(documentsPayload)) {
            VehicleDocumentDTO dto = new VehicleDocumentDTO();
            dto.setDocumentId(longValue(item.get("documentId")));
            dto.setIssuanceDate(parseDate(item.get("issuanceDate"), LocalDate.now()));
            dto.setExpirationDate(parseDate(item.get("expirationDate"), LocalDate.now().plusYears(1)));
            dto.setDocumentContent(stringValue(item.get("documentContent")));
            if (item.get("documentStatus") != null) {
                dto.setDocumentStatus(VehicleDocument.DocumentStatus.fromText(stringValue(item.get("documentStatus"))));
            }
            if (item.get("notificationEmail") != null) {
                dto.setNotificationEmail(stringValue(item.get("notificationEmail")));
            }

            Long vehicleDocumentId = null;
            if (item.get("vehicleDocumentId") != null) {
                try {
                    vehicleDocumentId = longValue(item.get("vehicleDocumentId"));
                } catch (Exception ignore) {}
            } else {
                try {
                    Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
                    for (VehicleDocument vd : vehicle.getDocuments()) {
                        if (vd.getDocument().getId().equals(dto.getDocumentId())) {
                            vehicleDocumentId = vd.getId();
                            break;
                        }
                    }
                } catch (Exception ignore) {}
            }

            if (vehicleDocumentId != null) {
                try {
                    vehicleService.updateVehicleDocument(vehicleId, vehicleDocumentId, dto);
                } catch (RuntimeException ex) {
                    vehicleService.addDocumentToVehicle(vehicleId, dto);
                }
            } else {
                vehicleService.addDocumentToVehicle(vehicleId, dto);
            }
            updated++;
        }
        return ResponseEntity.ok(Map.of("vehicleId", vehicleId, "documentosActualizados", updated));
    }

    /**
     * PUT /vehicles/{vehicleId}/documents/{vehicleDocumentId} - Actualiza un documento de un vehiculo.
     */
    @PutMapping("/{vehicleId}/documents/{vehicleDocumentId}")
    public ResponseEntity<VehicleDocumentResponseDTO> updateVehicleDocument(
            @PathVariable long vehicleId,
            @PathVariable long vehicleDocumentId,
            @Valid @RequestBody VehicleDocumentDTO vehicleDocumentDTO) {
        VehicleDocument vehicleDocument = vehicleService.updateVehicleDocument(
            vehicleId,
            vehicleDocumentId,
            vehicleDocumentDTO
        );
        return ResponseEntity.ok(mapDocumentToResponseDTO(vehicleDocument));
    }

    /**
     * DELETE /vehicles/{vehicleId}/documents/{vehicleDocumentId} - Elimina un documento de un vehiculo.
     */
    @DeleteMapping("/{vehicleId}/documents/{vehicleDocumentId}")
    public ResponseEntity<Void> removeDocumentFromVehicle(
            @PathVariable long vehicleId,
            @PathVariable long vehicleDocumentId) {
        vehicleService.removeDocumentFromVehicle(vehicleId, vehicleDocumentId);
        return ResponseEntity.noContent().build();
    }

    private VehicleResponseDTO mapToResponseDTO(Vehicle vehicle) {
        VehicleResponseDTO responseDTO = new VehicleResponseDTO();
        responseDTO.setId(vehicle.getId());
        responseDTO.setVehicleType(vehicle.getVehicleType());
        responseDTO.setLicensePlate(vehicle.getLicensePlate());
        responseDTO.setServiceType(vehicle.getServiceType());
        responseDTO.setFuelType(vehicle.getFuelType());
        responseDTO.setPassengerCapacity(vehicle.getPassengerCapacity());
        responseDTO.setColor(vehicle.getColor());
        responseDTO.setModelYear(vehicle.getModelYear());
        responseDTO.setBrand(vehicle.getBrand());
        responseDTO.setLine(vehicle.getLine());
        responseDTO.setDocuments(
            vehicle.getDocuments().stream()
                .map(this::mapDocumentToResponseDTO)
                .collect(Collectors.toSet())
        );
        return responseDTO;
    }

    private VehicleDocumentResponseDTO mapDocumentToResponseDTO(VehicleDocument vehicleDocument) {
        VehicleDocumentResponseDTO responseDTO = new VehicleDocumentResponseDTO();
        responseDTO.setId(vehicleDocument.getId());
        responseDTO.setDocumentId(vehicleDocument.getDocument().getId());
        responseDTO.setDocumentCode(vehicleDocument.getDocument().getDocumentCode());
        responseDTO.setDocumentName(vehicleDocument.getDocument().getDocumentName());
        responseDTO.setIssuanceDate(vehicleDocument.getIssuanceDate());
        responseDTO.setExpirationDate(vehicleDocument.getExpirationDate());
        responseDTO.setDocumentStatus(vehicleDocument.getDocumentStatus());
        return responseDTO;
    }

    private static Long longValue(Object value) {
        if (value instanceof Number n) {
            return n.longValue();
        }
        return value == null ? null : Long.valueOf(value.toString());
    }

    private static String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static LocalDate parseDate(Object value, LocalDate defaultValue) {
        return value == null ? defaultValue : LocalDate.parse(String.valueOf(value));
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> normalizeDocumentsPayload(Object payload) {
        if (payload instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        if (payload instanceof Map<?, ?> map && map.containsKey("documentos")) {
            return (List<Map<String, Object>>) map.get("documentos");
        }
        if (payload instanceof Map<?, ?> map) {
            return List.of((Map<String, Object>) map);
        }
        throw new IllegalArgumentException("Debe enviar un documento o una lista de documentos");
    }
}
