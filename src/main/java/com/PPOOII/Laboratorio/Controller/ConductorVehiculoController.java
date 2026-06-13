package com.PPOOII.Laboratorio.Controller;

import com.PPOOII.Laboratorio.Entities.ConductorVehiculo;
import com.PPOOII.Laboratorio.Services.LaboratorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/conductores")
public class ConductorVehiculoController {

    private final LaboratorioService laboratorioService;

    public ConductorVehiculoController(LaboratorioService laboratorioService) {
        this.laboratorioService = laboratorioService;
    }

    @PostMapping("/{conductorId}/vehiculos/{vehiculoId}")
    public ResponseEntity<Map<String, Object>> asociarVehiculo(
        @PathVariable int conductorId,
        @PathVariable long vehiculoId,
        @RequestBody(required = false) Map<String, String> body
    ) {
        ConductorVehiculo.EstadoConductor estado = body == null || body.get("estadoConductor") == null
            ? ConductorVehiculo.EstadoConductor.EA
            : ConductorVehiculo.EstadoConductor.valueOf(body.get("estadoConductor"));
        return ResponseEntity.ok(mapRelacion(
            laboratorioService.asociarConductorVehiculo(conductorId, vehiculoId, estado)
        ));
    }

    @PutMapping("/{conductorId}/vehiculos/{vehiculoId}/estado")
    public ResponseEntity<Map<String, Object>> cambiarEstado(
        @PathVariable int conductorId,
        @PathVariable long vehiculoId,
        @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.ok(mapRelacion(laboratorioService.cambiarEstadoConductorVehiculo(
            conductorId,
            vehiculoId,
            ConductorVehiculo.EstadoConductor.valueOf(body.get("estadoConductor"))
        )));
    }

    private Map<String, Object> mapRelacion(ConductorVehiculo relacion) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", relacion.getId());
        map.put("personaId", relacion.getConductor().getId());
        map.put("vehiculoId", relacion.getVehiculo().getId());
        map.put("placa", relacion.getVehiculo().getLicensePlate());
        map.put("estadoConductor", relacion.getEstado());
        return map;
    }
}
