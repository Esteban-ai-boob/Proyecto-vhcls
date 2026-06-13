package com.PPOOII.Laboratorio.Controller;

import com.PPOOII.Laboratorio.Entities.ConductorVehiculo;
import com.PPOOII.Laboratorio.Entities.ConductorVehiculo.EstadoConductor;
import com.PPOOII.Laboratorio.Entities.Persona;
import com.PPOOII.Laboratorio.Entities.Trayecto;
import com.PPOOII.Laboratorio.Entities.Usuario;
import com.PPOOII.Laboratorio.Repository.IPersonaRepository;
import com.PPOOII.Laboratorio.Services.LaboratorioService;
import com.vehiclemanagement.entity.Vehicle;
import com.vehiclemanagement.entity.VehicleDocument;
import com.vehiclemanagement.repository.VehicleDocumentRepository;
import com.vehiclemanagement.repository.VehicleRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "1. API Laboratorio V1", description = "Endpoints integrados principales para la evaluación del laboratorio (Personas, Conductores, Vehículos, Rutas y Consultas Públicas)")
@RestController
@RequestMapping("/LaboratorioV1")
@CrossOrigin(origins = {"http://localhost", "http://127.0.0.1", "null"})
public class LaboratorioController {

    private final LaboratorioService laboratorioService;
    private final IPersonaRepository personaRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleDocumentRepository vehicleDocumentRepository;

    public LaboratorioController(
        LaboratorioService laboratorioService,
        IPersonaRepository personaRepository,
        VehicleRepository vehicleRepository,
        VehicleDocumentRepository vehicleDocumentRepository
    ) {
        this.laboratorioService = laboratorioService;
        this.personaRepository = personaRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleDocumentRepository = vehicleDocumentRepository;
    }

    @GetMapping("/personas")
    public List<Persona> listarPersonas() {
        return personaRepository.findAll();
    }

    @PostMapping("/personas")
    public ResponseEntity<?> crearPersona(@RequestBody Map<String, Object> body) {
        try {
            Persona persona = mapPersona(new Persona(), body);
            return ResponseEntity.ok(laboratorioService.crearPersona(persona, stringValue(body.get("licenciaBase64"))));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            Throwable cause = e.getMostSpecificCause();
            String dbError = cause != null ? cause.getMessage() : e.getMessage();
            return ResponseEntity.badRequest().body(Map.of("error", "Error en Base de Datos: " + dbError));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PutMapping("/personas/{id}")
    public Persona actualizarPersona(@PathVariable int id, @RequestBody Map<String, Object> body) {
        Persona persona = mapPersona(new Persona(), body);
        return laboratorioService.actualizarPersona(id, persona, stringValue(body.get("licenciaBase64")));
    }

    @DeleteMapping("/personas/{numeroIdentificacion}")
    public ResponseEntity<Void> eliminarPersona(@PathVariable String numeroIdentificacion) {
        laboratorioService.eliminarPersona(numeroIdentificacion);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/usuarios/{login}/password")
    public ResponseEntity<Map<String, Object>> cambiarPassword(
        @PathVariable String login,
        @RequestBody Map<String, String> body
    ) {
        Usuario usuario = laboratorioService.cambiarPassword(login, body.get("password"));
        return ResponseEntity.ok(mapUsuario(usuario));
    }

    @GetMapping("/usuarios/{login}/apikey/regenerar")
    public ResponseEntity<Map<String, Object>> regenerarApiKey(@PathVariable String login) {
        Usuario usuario = laboratorioService.regenerarApiKey(login);
        return ResponseEntity.ok(mapUsuario(usuario));
    }

    @PostMapping("/conductores/{conductorId}/vehiculos/{vehiculoId}")
    public ResponseEntity<Map<String, Object>> asociarConductorVehiculo(
        @PathVariable int conductorId,
        @PathVariable long vehiculoId,
        @RequestParam(defaultValue = "EA") EstadoConductor estado
    ) {
        return ResponseEntity.ok(mapConductorVehiculo(
            laboratorioService.asociarConductorVehiculo(conductorId, vehiculoId, estado)
        ));
    }

    @PutMapping("/conductores/{conductorId}/vehiculos/{vehiculoId}/estado")
    public ResponseEntity<Map<String, Object>> cambiarEstadoConductorVehiculo(
        @PathVariable int conductorId,
        @PathVariable long vehiculoId,
        @RequestParam EstadoConductor estado
    ) {
        return ResponseEntity.ok(mapConductorVehiculo(
            laboratorioService.cambiarEstadoConductorVehiculo(conductorId, vehiculoId, estado)
        ));
    }

    @PostMapping("/trayectos")
    public ResponseEntity<Map<String, Object>> crearTrayecto(@RequestBody Map<String, Object> body) {
        if (!body.containsKey("vehiculoId") || !body.containsKey("conductorId")) {
            return ResponseEntity.badRequest().body(Map.of("error", "vehiculoId y conductorId son obligatorios"));
        }
        
        Integer orden;
        if (body.containsKey("orden") && body.get("orden") != null) {
            orden = intValue(body.get("orden"));
        } else {
            orden = laboratorioService.obtenerSiguienteOrdenRuta(stringValue(body.get("codigoRuta")));
        }

        Trayecto trayecto = new Trayecto();
        trayecto.setCodigoRuta(stringValue(body.get("codigoRuta")));
        trayecto.setOrden(orden);
        trayecto.setNombreParada(stringValue(body.get("nombreParada")));
        trayecto.setUbicacion(stringValue(body.get("ubicacion")));
        if (body.get("latitud") == null || body.get("longitud") == null || 
            body.get("latitud").toString().isBlank() || body.get("longitud").toString().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "La latitud y longitud son obligatorias y no pueden estar vacías"));
        }
        trayecto.setLatitud(doubleValue(body.get("latitud")));
        trayecto.setLongitud(doubleValue(body.get("longitud")));
        try {
            long vehiculoId = longValue(body.get("vehiculoId"));
            int conductorId = intValue(body.get("conductorId"));
            return ResponseEntity.ok(laboratorioService.crearYMapearTrayecto(trayecto, vehiculoId, conductorId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/trayectos/{id}")
    public ResponseEntity<?> eliminarTrayecto(@PathVariable @NonNull Long id) {
        try {
            laboratorioService.eliminarTrayecto(id);
            return ResponseEntity.ok(Map.of("message", "Trayecto eliminado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/rutas/{codigoRuta}")
    public List<Map<String, Object>> consultarRuta(@PathVariable String codigoRuta) {
        return laboratorioService.consultarRutaMapeada(codigoRuta);
    }

    @GetMapping("/conductores/{numeroIdentificacion}/rutas")
    public List<String> consultarCodigosRutaPorConductor(@PathVariable String numeroIdentificacion) {
        return laboratorioService.consultarCodigosRutaPorConductor(numeroIdentificacion);
    }

    @GetMapping("/vehiculos/{placa}/rutas")
    public Map<String, List<Map<String, Object>>> consultarRutasPorPlaca(@PathVariable String placa) {
        return laboratorioService.consultarRutasPorPlaca(placa);
    }

    @GetMapping("/rutas/bloqueadas")
    public List<Map<String, Object>> consultarRutasConBloqueos() {
        return laboratorioService.consultarRutasConBloqueosMapeadas();
    }

    @GetMapping("/public/vehiculos/documentos-vencidos")
    public List<Vehicle> vehiculosConDocumentosVencidos() {
        return vehicleRepository.findVehiclesByDocumentStatus(VehicleDocument.DocumentStatus.VENDIDO_VENCIDO);
    }

    @GetMapping("/public/conductores/pueden-operar")
    public List<Map<String, Object>> conductoresQuePuedenOperar() {
        return laboratorioService.consultarConductoresQuePuedenOperar().stream()
            .map(this::mapConductorVehiculo)
            .toList();
    }

    @GetMapping("/public/vehiculos/placa/{placa}")
    public Map<String, Object> vehiculoPorPlaca(@PathVariable String placa) {
        Vehicle vehiculo = vehicleRepository.findByLicensePlateWithDocuments(placa)
            .orElseThrow(() -> new IllegalArgumentException("Vehiculo no encontrado: " + placa));
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", vehiculo.getId());
        map.put("placa", vehiculo.getLicensePlate());
        map.put("tipo", vehiculo.getVehicleType());
        map.put("documentos", vehiculo.getDocuments());
        map.put("rutas", laboratorioService.consultarRutasPorPlaca(placa));
        return map;
    }

    @GetMapping("/public/vehiculos/documentos-por-vencer")
    public List<VehicleDocument> documentosPorVencer(@RequestParam(defaultValue = "30") int dias) {
        LocalDate hoy = laboratorioService.hoy();
        LocalDate hasta = hoy.plusDays(dias);
        return vehicleDocumentRepository.findAll().stream()
            .filter(vd -> !vd.getExpirationDate().isBefore(hoy) && !vd.getExpirationDate().isAfter(hasta))
            .toList();
    }

    @GetMapping("/public/personas/total-por-tipo")
    public Map<String, Long> totalPersonasPorTipo() {
        return laboratorioService.totalPersonasPorTipo();
    }

    @PostMapping("/vehicles/{vehicleId}/documents/batch-base64")
    public ResponseEntity<Map<String, Object>> cargarDocumentosBase64(
        @PathVariable long vehicleId,
        @RequestBody List<Map<String, Object>> documentos
    ) {
        int actualizados = 0;
        for (Map<String, Object> item : documentos) {
            Long documentId = longValue(item.get("documentId"));
            VehicleDocument vd = vehicleDocumentRepository.findByVehicleIdAndDocumentId(vehicleId, documentId)
                .orElseThrow(() -> new IllegalArgumentException("Documento no asociado al vehiculo: " + documentId));
            String base64 = stringValue(item.get("documentContent"));
            vd.setDocumentContent(base64);
            vd.setDocumentBlob(Base64.getDecoder().decode(base64));
            if (item.get("expirationDate") != null) {
                vd.setExpirationDate(LocalDate.parse(stringValue(item.get("expirationDate"))));
            }
            if (item.get("issuanceDate") != null) {
                vd.setIssuanceDate(LocalDate.parse(stringValue(item.get("issuanceDate"))));
            }
            if (item.get("documentStatus") != null) {
                vd.setDocumentStatus(VehicleDocument.DocumentStatus.fromText(stringValue(item.get("documentStatus"))));
            }
            vehicleDocumentRepository.save(vd);
            actualizados++;
        }
        return ResponseEntity.ok(Map.of("vehicleId", vehicleId, "documentosActualizados", actualizados));
    }


    private @NonNull Persona mapPersona(@NonNull Persona persona, Map<String, Object> body) {
        if (body.get("id") != null) {
            persona.setId(intValue(body.get("id")));
        }
        persona.setPnombre(stringValue(body.get("pnombre")));
        persona.setNombre(stringValue(body.get("pnombre")));
        persona.setUbicacion(valueOrDefault(body, "ubicacion", "No especificada"));
        if (body.get("edad") != null) {
            persona.setEdad(intValue(body.get("edad")));
        }
        persona.setTipoIdentificacion(valueOrDefault(body, "tipoIdentificacion", "CC"));
        persona.setNumeroIdentificacion(stringValue(body.get("numeroIdentificacion")));
        persona.setTipoPersona(valueOrDefault(body, "tipoPersona", "CLIENTE"));
        if (body.get("fechaLicencia") != null) {
            persona.setFechaLicencia(LocalDate.parse(stringValue(body.get("fechaLicencia"))));
        }
        return persona;
    }

    private Map<String, Object> mapUsuario(Usuario usuario) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", usuario.getId());
        map.put("login", usuario.getLogin());
        map.put("persona", usuario.getPersona().getId());
        map.put("apikey", usuario.getApikey());
        return map;
    }

    private Map<String, Object> mapConductorVehiculo(ConductorVehiculo relacion) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", relacion.getId());
        map.put("conductorId", relacion.getConductor().getId());
        map.put("conductor", relacion.getConductor().getPnombre());
        map.put("vehiculoId", relacion.getVehiculo().getId());
        map.put("placa", relacion.getVehiculo().getLicensePlate());
        map.put("estado", relacion.getEstado());
        return map;
    }

    private static String valueOrDefault(Map<String, Object> body, String key, String defaultValue) {
        String value = stringValue(body.get(key));
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static int intValue(Object value) {
        return Integer.parseInt(String.valueOf(value));
    }

    private static long longValue(Object value) {
        return Long.parseLong(String.valueOf(value));
    }

    private static double doubleValue(Object value) {
        return Double.parseDouble(String.valueOf(value));
    }
}
