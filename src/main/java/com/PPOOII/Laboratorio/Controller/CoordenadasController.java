package com.PPOOII.Laboratorio.Controller;

import com.PPOOII.Laboratorio.Entities.Coordenadas;
import com.PPOOII.Laboratorio.Services.Interfaces.ICoordenadasService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/LaboratorioV1")
@CrossOrigin(origins = {"http://localhost", "http://127.0.0.1", "null"})
public class CoordenadasController {

    private final ICoordenadasService coordenadaService;

    public CoordenadasController(@Qualifier("CoordenadasService") ICoordenadasService coordenadaService) {
        this.coordenadaService = coordenadaService;
    }

    @GetMapping("/coordenadas")
    public List<Coordenadas> consultarAllCoordenadas(@NonNull Pageable pageable) {
        return coordenadaService.consultarAllCoordenadas(pageable);
    }
}
