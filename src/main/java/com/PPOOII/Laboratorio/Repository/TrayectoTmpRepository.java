package com.PPOOII.Laboratorio.Repository;

import com.PPOOII.Laboratorio.Entities.TrayectoTmp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrayectoTmpRepository extends JpaRepository<TrayectoTmp, Long> {

    List<TrayectoTmp> findByIdCargueOrderByIdAsc(Double idCargue);
}
