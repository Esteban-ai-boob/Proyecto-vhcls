package com.vehiclemanagement.repository;

import com.vehiclemanagement.entity.VehicleDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad VehicleDocument.
 * Proporciona métodos CRUD y consultas personalizadas.
 */
public interface VehicleDocumentRepository extends JpaRepository<VehicleDocument, Long> {

    /**
     * Busca todos los documentos asociados a un vehículo específico.
     */
    List<VehicleDocument> findByVehicleId(Long vehicleId);

    /**
     * Busca un documento específico de un vehículo.
     */
    Optional<VehicleDocument> findByVehicleIdAndDocumentId(Long vehicleId, Long documentId);

    /**
     * Verifica si un vehículo tiene un documento asociado.
     */
    boolean existsByVehicleIdAndDocumentId(Long vehicleId, Long documentId);

    /**
     * Busca documentos con un estado específico para un vehículo.
     */
    List<VehicleDocument> findByVehicleIdAndDocumentStatus(Long vehicleId, 
        com.vehiclemanagement.entity.VehicleDocument.DocumentStatus status);

    /**
     * Cuenta documentos por estado de un vehículo.
     */
    @Query("SELECT COUNT(vd) FROM VehicleDocument vd WHERE vd.vehicle.id = :vehicleId")
    long countByVehicleId(@Param("vehicleId") Long vehicleId);

    @Query(value = """
        SELECT *
        FROM vehiculo_documento
        WHERE fecha_vencimiento < CURRENT_DATE
          AND estado <> 'Vendido/Vencido'
        """, nativeQuery = true)
    List<VehicleDocument> findVencidosSinMarcar();
}
