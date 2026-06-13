package com.PPOOII.Laboratorio.Services.Interfaces;

import com.PPOOII.Laboratorio.Entities.Coordenadas;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import java.util.List;

public interface ICoordenadasService {

    List<Coordenadas> consultarAllCoordenadas(@NonNull Pageable pageable);
}
