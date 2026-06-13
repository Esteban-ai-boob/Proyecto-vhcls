package com.PPOOII.Laboratorio.Repository;

import com.PPOOII.Laboratorio.Entities.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("IPersonaRepository")
public interface IPersonaRepository extends JpaRepository<Persona, Integer> {

    @Query("SELECT per FROM PER per")
    List<Persona> getPersonas();

    @Query("SELECT COALESCE(MAX(p.id), 0) + 1 FROM PER p")
    int getNextId();

    Optional<Persona> findByNumeroIdentificacion(String numeroIdentificacion);

    List<Persona> findByTipoPersonaIgnoreCase(String tipoPersona);

    @Query("SELECT per.tipoPersona, COUNT(per) FROM PER per GROUP BY per.tipoPersona")
    List<Object[]> countPersonasByTipo();

    @Query("SELECT per FROM PER per WHERE UPPER(per.tipoPersona) = 'CONDUCTOR' AND per.fechaLicencia < CURRENT_DATE")
    List<Persona> findConductoresConLicenciaVencida();

    @Procedure(procedureName = "ppooii.CARGAR_DATOS_EXCEL")
    void CARGAR_DATOS_EXCEL(
        @Param("P_NOMBRE") String nombre,
        @Param("P_EDAD") String edad,
        @Param("P_UBICACION") String ubicacion,
        @Param("P_ID_CARGUE") Double idCargue
    );

    @Procedure(procedureName = "ppooii.VALIDAR_DATOS_CARGUE")
    void VALIDAR_DATOS_CARGUE(@Param("P_ID_CARGUE") Double idCargue);

    @Procedure(procedureName = "ppooii.PROCESAR_DATOS_EXCEL")
    void PROCESAR_DATOS_EXCEL(@Param("P_ID_CARGUE") Double idCargue);
}
