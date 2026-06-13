package com.PPOOII.Laboratorio.Controller;

import com.PPOOII.Laboratorio.Dto.RutaDetalleDTO;
import com.PPOOII.Laboratorio.Dto.RutaParadaDTO;
import com.PPOOII.Laboratorio.Entities.Trayecto;
import com.PPOOII.Laboratorio.Services.LaboratorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Rutas", description = "Consultas protegidas de trayectos y rutas")
@SecurityRequirement(name = "bearerAuth")
@SecurityRequirement(name = "apiKeyAuth")
public class RutaController {

    private final LaboratorioService laboratorioService;

    public RutaController(LaboratorioService laboratorioService) {
        this.laboratorioService = laboratorioService;
    }

    @Operation(summary = "Visualiza en orden los trayectos e informacion asociada de una ruta")
    @GetMapping("/rutas/{codigoRuta}")
    public RutaDetalleDTO consultarRuta(@PathVariable String codigoRuta) {
        return mapRuta(laboratorioService.consultarRuta(codigoRuta));
    }

    @Operation(summary = "Visualiza los codigos de rutas agrupados por conductor")
    @GetMapping("/conductores/{identificacion}/rutas")
    public Map<String, Object> consultarRutasPorConductor(@PathVariable String identificacion) {
        return Map.of(
            "identificacionConductor", identificacion,
            "codigosRuta", laboratorioService.consultarCodigosRutaPorConductor(identificacion)
        );
    }

    @Operation(summary = "Visualiza codigo de ruta y conductor asociado agrupados por placa")
    @GetMapping("/vehiculos/{placa}/rutas")
    public Map<String, List<Map<String, Object>>> consultarRutasPorPlaca(@PathVariable String placa) {
        return laboratorioService.consultarRutasPorPlaca(placa);
    }

    @Operation(summary = "Consulta trayectos cuyo vehiculo no este habilitado o cuyo conductor este restringido")
    @GetMapping("/rutas/no-habilitadas")
    public List<Map<String, Object>> consultarRutasNoHabilitadas() {
        return laboratorioService.consultarRutasConBloqueos().stream()
            .map(LaboratorioService::mapTrayecto)
            .toList();
    }

    private RutaDetalleDTO mapRuta(List<Trayecto> trayectos) {
        if (trayectos.isEmpty()) {
            throw new IllegalArgumentException("Ruta no encontrada");
        }

        Trayecto primero = trayectos.get(0);
        RutaDetalleDTO ruta = new RutaDetalleDTO();
        ruta.setCodigoRuta(primero.getCodigoRuta());
        ruta.setPlaca(primero.getVehiculo().getLicensePlate());
        ruta.setConductor(primero.getConductor().getPnombre());
        ruta.setIdentificacionConductor(primero.getConductor().getNumeroIdentificacion());
        ruta.setParadas(trayectos.stream().map(this::mapParada).toList());
        return ruta;
    }

    private RutaParadaDTO mapParada(Trayecto trayecto) {
        RutaParadaDTO parada = new RutaParadaDTO();
        parada.setOrden(trayecto.getOrden());
        parada.setNombreParada(trayecto.getNombreParada());
        parada.setUbicacion(trayecto.getUbicacion());
        parada.setLatitud(trayecto.getLatitud());
        parada.setLongitud(trayecto.getLongitud());
        return parada;
    }
}
