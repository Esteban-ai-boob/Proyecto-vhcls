package com.PPOOII.Laboratorio.Services;

import com.PPOOII.Laboratorio.Entities.Coordenadas;
import com.PPOOII.Laboratorio.Repository.CoordenadasRepository;
import com.PPOOII.Laboratorio.Services.Interfaces.ICoordenadasService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("CoordenadasService")
public class CoordenadasServiceImpl implements ICoordenadasService {

    private static final Logger Logger = LogManager.getLogger(CoordenadasServiceImpl.class);

    private final CoordenadasRepository coordenadasRepository;

    public CoordenadasServiceImpl(@Qualifier("ICoordenadasRepository") CoordenadasRepository coordenadasRepository) {
        this.coordenadasRepository = coordenadasRepository;
    }

    @Override
    public List<Coordenadas> consultarAllCoordenadas(@NonNull Pageable pageable) {
        Logger.debug("Consultando coordenadas con paginacion: {}", pageable);
        return coordenadasRepository.findAll(pageable).getContent();
    }
}
