package com.PPOOII.Laboratorio.Repository;

import com.PPOOII.Laboratorio.Entities.ConductorVehiculo;
import com.PPOOII.Laboratorio.Entities.ConductorVehiculo.EstadoConductor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConductorVehiculoRepository extends JpaRepository<ConductorVehiculo, Long> {

    Optional<ConductorVehiculo> findByConductor_IdAndVehiculo_Id(Integer conductorId, Long vehiculoId);

    List<ConductorVehiculo> findByEstado(EstadoConductor estado);

    List<ConductorVehiculo> findByConductor_Id(Integer conductorId);

    @Query("SELECT cv FROM ConductorVehiculo cv JOIN FETCH cv.conductor JOIN FETCH cv.vehiculo WHERE cv.vehiculo.licensePlate = :placa")
    List<ConductorVehiculo> findByPlaca(@Param("placa") String placa);
}
