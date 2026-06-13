package com.PPOOII.Laboratorio.Repository;

import com.PPOOII.Laboratorio.Entities.Coordenadas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository("ICoordenadasRepository")
public interface CoordenadasRepository extends JpaRepository<Coordenadas, Integer> {

    @Query("SELECT coord FROM COOR coord WHERE coord.persona = :id_persona")
    Coordenadas getCoordenadaXPersona(@Param("id_persona") int persona);
}

