package com.vehiclemanagement.repository;

import com.vehiclemanagement.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Vehicle.
 * Proporciona métodos CRUD y consultas personalizadas.
 */
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    /**
     * Busca un vehículo por número de placa.
     */
    Optional<Vehicle> findByLicensePlate(String licensePlate);

    /**
     * Busca todos los vehículos de un tipo específico.
     */
    List<Vehicle> findByVehicleType(Vehicle.VehicleType vehicleType);

    /**
     * Busca vehículos que tengan asociado un documento específico.
     */
    @Query("SELECT DISTINCT v FROM Vehicle v " +
           "LEFT JOIN FETCH v.documents vd " +
           "LEFT JOIN FETCH vd.document d " +
           "WHERE vd.document.id = :documentId")
    List<Vehicle> findVehiclesByDocumentId(@Param("documentId") Long documentId);

    /**
     * Busca vehículos según el estado del documento asociado.
     */
    @Query("SELECT DISTINCT v FROM Vehicle v " +
           "LEFT JOIN FETCH v.documents vd " +
           "LEFT JOIN FETCH vd.document d " +
           "WHERE vd.documentStatus = :status")
    List<Vehicle> findVehiclesByDocumentStatus(@Param("status") com.vehiclemanagement.entity.VehicleDocument.DocumentStatus status);

    /**
     * Busca todos los vehículos (pagination no incluida aquí pero se puede agregar).
     */
    @Query("SELECT DISTINCT v FROM Vehicle v " +
           "LEFT JOIN FETCH v.documents vd " +
           "LEFT JOIN FETCH vd.document d")
    List<Vehicle> findAllWithDocuments();

    @Query("SELECT DISTINCT v FROM Vehicle v " +
           "LEFT JOIN FETCH v.documents vd " +
           "LEFT JOIN FETCH vd.document d " +
           "WHERE v.id = :id")
    Optional<Vehicle> findByIdWithDocuments(@Param("id") Long id);

    @Query("SELECT DISTINCT v FROM Vehicle v " +
           "LEFT JOIN FETCH v.documents vd " +
           "LEFT JOIN FETCH vd.document d " +
           "WHERE UPPER(v.licensePlate) = UPPER(:licensePlate)")
    Optional<Vehicle> findByLicensePlateWithDocuments(@Param("licensePlate") String licensePlate);

    @Query("SELECT DISTINCT v FROM Vehicle v " +
           "LEFT JOIN FETCH v.documents vd " +
           "LEFT JOIN FETCH vd.document d " +
           "WHERE v.vehicleType = :vehicleType")
    List<Vehicle> findByVehicleTypeWithDocuments(@Param("vehicleType") Vehicle.VehicleType vehicleType);

    /**
     * Verifica si existe un vehículo con una placa específica.
     */
    boolean existsByLicensePlate(String licensePlate);
}
