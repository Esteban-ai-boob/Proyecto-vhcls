package com.PPOOII.Laboratorio.Repository;

import com.PPOOII.Laboratorio.Entities.Trayecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrayectoRepository extends JpaRepository<Trayecto, Long> {

    @Query("SELECT t FROM Trayecto t JOIN FETCH t.vehiculo JOIN FETCH t.conductor WHERE t.codigoRuta = :codigoRuta ORDER BY t.orden")
    List<Trayecto> findByCodigoRutaOrder(@Param("codigoRuta") String codigoRuta);

    @Query("SELECT COALESCE(MAX(t.orden), 0) FROM Trayecto t WHERE t.codigoRuta = :codigoRuta")
    Integer findMaxOrdenByCodigoRuta(@Param("codigoRuta") String codigoRuta);

    boolean existsByCodigoRuta(String codigoRuta);

    @Query("SELECT DISTINCT t.codigoRuta FROM Trayecto t WHERE t.conductor.numeroIdentificacion = :numeroIdentificacion ORDER BY t.codigoRuta")
    List<String> findCodigosRutaByConductor(@Param("numeroIdentificacion") String numeroIdentificacion);

    @Query("SELECT DISTINCT t FROM Trayecto t JOIN FETCH t.vehiculo JOIN FETCH t.conductor WHERE UPPER(t.vehiculo.licensePlate) = UPPER(:placa) ORDER BY t.codigoRuta, t.orden")
    List<Trayecto> findByPlaca(@Param("placa") String placa);

    @Query(value = """
        SELECT DISTINCT t.*
        FROM trayecto t
        LEFT JOIN conductor_vehiculo cv
          ON cv.persona_id = t.conductor_id
         AND cv.vehicle_id = t.vehicle_id
        WHERE cv.estado = 'RO'
           OR EXISTS (
                SELECT 1 FROM vehiculo_documento vd
                WHERE vd.vehiculo_id = t.vehicle_id
                  AND vd.estado <> 'Habilitado'
           )
        ORDER BY t.codigo_ruta, t.orden
        """, nativeQuery = true)
    List<Trayecto> findRutasConBloqueos();

    @Query("SELECT t FROM Trayecto t WHERE t.latitud IS NULL OR t.longitud IS NULL")
    List<Trayecto> findSinCoordenadas();
}
