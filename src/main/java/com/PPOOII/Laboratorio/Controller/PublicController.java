package com.PPOOII.Laboratorio.Controller;

import com.PPOOII.Laboratorio.Entities.ConductorVehiculo;
import com.PPOOII.Laboratorio.Entities.Trayecto;
import com.PPOOII.Laboratorio.Repository.ConductorVehiculoRepository;
import com.PPOOII.Laboratorio.Services.LaboratorioService;
import com.vehiclemanagement.entity.Vehicle;
import com.vehiclemanagement.entity.VehicleDocument;
import com.vehiclemanagement.repository.VehicleDocumentRepository;
import com.vehiclemanagement.repository.VehicleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/public", "/LaboratorioV1/public"})
@CrossOrigin(origins = {"http://localhost", "http://127.0.0.1", "null"})
@Tag(name = "Consultas publicas", description = "Endpoints de consulta para demos, tableros y mapa operativo")
public class PublicController {

    private final VehicleRepository vehicleRepository;
    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final ConductorVehiculoRepository conductorVehiculoRepository;
    private final LaboratorioService laboratorioService;

    public PublicController(
        VehicleRepository vehicleRepository,
        VehicleDocumentRepository vehicleDocumentRepository,
        ConductorVehiculoRepository conductorVehiculoRepository,
        LaboratorioService laboratorioService
    ) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleDocumentRepository = vehicleDocumentRepository;
        this.conductorVehiculoRepository = conductorVehiculoRepository;
        this.laboratorioService = laboratorioService;
    }

    @GetMapping("/vehiculos/vencidos")
    @Operation(summary = "Lista vehiculos con documentos vencidos")
    public List<Vehicle> vehiculosVencidos() {
        return vehicleRepository.findVehiclesByDocumentStatus(VehicleDocument.DocumentStatus.VENDIDO_VENCIDO);
    }

    @GetMapping("/conductores/operar")
    @Operation(summary = "Lista conductores que pueden operar vehiculos")
    public List<Map<String, Object>> conductoresOperar() {
        return conductorVehiculoRepository.findByEstado(ConductorVehiculo.EstadoConductor.PO).stream()
            .map(this::mapConductorVehiculo)
            .toList();
    }

    @GetMapping("/vehiculos/{placa}/detalle")
    @Operation(summary = "Consulta detalle publico de un vehiculo por placa")
    public Map<String, Object> detalleVehiculo(@PathVariable String placa) {
        Vehicle vehiculo = vehicleRepository.findByLicensePlateWithDocuments(placa)
            .orElseThrow(() -> new IllegalArgumentException("Vehiculo no encontrado: " + placa));

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", vehiculo.getId());
        map.put("placa", vehiculo.getLicensePlate());
        map.put("tipoVehiculo", vehiculo.getVehicleType());
        map.put("tipoServicio", vehiculo.getServiceType());
        map.put("tipoCombustible", vehiculo.getFuelType());
        map.put("documentos", vehiculo.getDocuments());
        map.put("conductores", conductorVehiculoRepository.findByPlaca(placa).stream()
            .map(this::mapConductorVehiculo)
            .toList());
        return map;
    }

    @GetMapping("/vehiculos/por-vencer")
    @Operation(summary = "Lista documentos de vehiculos proximos a vencer")
    public List<VehicleDocument> vehiculosPorVencer(@RequestParam(defaultValue = "30") int dias) {
        LocalDate hoy = laboratorioService.hoy();
        LocalDate hasta = hoy.plusDays(dias);
        return vehicleDocumentRepository.findAll().stream()
            .filter(vd -> vd.getExpirationDate() != null)
            .filter(vd -> !vd.getExpirationDate().isBefore(hoy) && !vd.getExpirationDate().isAfter(hasta))
            .toList();
    }

    @GetMapping("/personas/conteo-grupo")
    @Operation(summary = "Cuenta personas agrupadas por tipo")
    public Map<String, Long> conteoGrupoPersonas() {
        return laboratorioService.totalPersonasPorTipo();
    }

    @GetMapping("/rutas/{codigoRuta}/detalle")
    @Operation(summary = "Consulta detalle publico de una ruta para renderizarla en mapa")
    public Map<String, Object> detalleRuta(@PathVariable String codigoRuta) {
        List<Trayecto> trayectos = laboratorioService.consultarRuta(codigoRuta);
        if (trayectos.isEmpty()) {
            throw new IllegalArgumentException("Ruta no encontrada: " + codigoRuta);
        }

        Trayecto primero = trayectos.get(0);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("codigoRuta", primero.getCodigoRuta());
        map.put("placa", primero.getVehiculo().getLicensePlate());
        map.put("conductor", primero.getConductor().getPnombre());
        map.put("identificacionConductor", primero.getConductor().getNumeroIdentificacion());
        map.put("totalParadas", trayectos.size());
        map.put("paradas", trayectos.stream().map(LaboratorioService::mapTrayecto).toList());
        return map;
    }

    private Map<String, Object> mapConductorVehiculo(ConductorVehiculo relacion) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", relacion.getId());
        map.put("personaId", relacion.getConductor().getId());
        map.put("nombre", relacion.getConductor().getPnombre());
        map.put("vehiculoId", relacion.getVehiculo().getId());
        map.put("placa", relacion.getVehiculo().getLicensePlate());
        map.put("estadoConductor", relacion.getEstado());
        return map;
    }
}
