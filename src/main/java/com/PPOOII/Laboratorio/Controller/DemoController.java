package com.PPOOII.Laboratorio.Controller;

import com.PPOOII.Laboratorio.Entities.Coordenadas;
import com.PPOOII.Laboratorio.Entities.Persona;
import com.PPOOII.Laboratorio.Repository.CoordenadasRepository;
import com.PPOOII.Laboratorio.Repository.IPersonaRepository;
import com.vehiclemanagement.entity.Vehicle;
import com.vehiclemanagement.repository.VehicleRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "8. Demo y Pruebas", description = "Endpoints de demostración para validar la estructura del sistema")
@RestController
@RequestMapping("/api/demo")
@CrossOrigin(origins = {"http://localhost", "http://127.0.0.1", "null"})
public class DemoController {

    private final IPersonaRepository personaRepository;
    private final CoordenadasRepository coordenadasRepository;
    private final VehicleRepository vehicleRepository;
    private final EntityManager entityManager;

    public DemoController(
        IPersonaRepository personaRepository,
        CoordenadasRepository coordenadasRepository,
        VehicleRepository vehicleRepository,
        EntityManager entityManager
    ) {
        this.personaRepository = personaRepository;
        this.coordenadasRepository = coordenadasRepository;
        this.vehicleRepository = vehicleRepository;
        this.entityManager = entityManager;
    }

    @PostMapping("/personas")
    @Transactional
    public ResponseEntity<Map<String, Object>> crearPersona(@RequestBody Map<String, Object> body) {
        Persona persona = new Persona();
        persona.setId(nextPersonaId());
        persona.setPnombre(required(body, "pnombre"));
        persona.setUbicacion(required(body, "ubicacion"));
        if (body.get("edad") != null && !stringValue(body.get("edad")).isBlank()) {
            persona.setEdad(Integer.valueOf(stringValue(body.get("edad"))));
        }
        persona.setTipoIdentificacion(valueOrDefault(body, "tipoIdentificacion", "CC"));
        persona.setNumeroIdentificacion(valueOrDefault(body, "numeroIdentificacion", "DEMO-" + persona.getId()));
        persona.setTipoPersona(valueOrDefault(body, "tipoPersona", "CLIENTE"));
        if (body.get("fechaLicencia") != null && !stringValue(body.get("fechaLicencia")).isBlank()) {
            persona.setFechaLicencia(LocalDate.parse(stringValue(body.get("fechaLicencia"))));
        }

        Persona saved = personaRepository.save(persona);
        return ResponseEntity.ok(withJsonLink("persona", saved.getId(), mapPersona(saved)));
    }

    @PostMapping("/puntos")
    @Transactional
    public ResponseEntity<Map<String, Object>> crearPuntoRuta(@RequestBody Map<String, Object> body) {
        Persona persona = new Persona();
        persona.setId(nextPersonaId());
        persona.setPnombre(required(body, "nombre"));
        persona.setUbicacion(required(body, "ubicacion"));
        if (body.get("edad") != null && !stringValue(body.get("edad")).isBlank()) {
            persona.setEdad(Integer.valueOf(stringValue(body.get("edad"))));
        }
        persona.setTipoIdentificacion("CC");
        persona.setNumeroIdentificacion(valueOrDefault(body, "numeroIdentificacion", "RUTA-" + persona.getId()));
        persona.setTipoPersona(valueOrDefault(body, "tipoPersona", "CLIENTE"));
        Persona savedPersona = personaRepository.save(persona);

        double latitud = doubleValue(body.get("latitud"));
        double longitud = doubleValue(body.get("longitud"));
        Coordenadas coordenada = coordenadasRepository.save(new Coordenadas(
            savedPersona.getId(),
            savedPersona.getPnombre(),
            longitud,
            latitud
        ));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("persona", mapPersona(savedPersona));
        response.put("coordenada", mapCoordenada(coordenada));
        response.put("jsonLink", localUrl("/api/demo/puntos/" + coordenada.getId()));
        response.put("rutaLink", localUrl("/LaboratorioV1/coordenadas"));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/vehiculos")
    @Transactional
    public ResponseEntity<Map<String, Object>> crearVehiculo(@RequestBody Map<String, Object> body) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleType(Vehicle.VehicleType.valueOf(valueOrDefault(body, "vehicleType", "AUTOMOVIL")));
        vehicle.setLicensePlate(required(body, "licensePlate"));
        vehicle.setServiceType(Vehicle.ServiceType.valueOf(valueOrDefault(body, "serviceType", "PRIVADO")));
        vehicle.setFuelType(Vehicle.FuelType.valueOf(valueOrDefault(body, "fuelType", "GASOLINA")));
        vehicle.setPassengerCapacity(Integer.valueOf(valueOrDefault(body, "passengerCapacity", "5")));
        vehicle.setColor(valueOrDefault(body, "color", "#2563EB"));
        vehicle.setModelYear(Integer.valueOf(valueOrDefault(body, "modelYear", String.valueOf(LocalDate.now().getYear()))));
        vehicle.setBrand(valueOrDefault(body, "brand", "Demo"));
        vehicle.setLine(valueOrDefault(body, "line", "Ruta"));

        Vehicle saved = vehicleRepository.save(vehicle);
        return ResponseEntity.ok(withJsonLink("vehicle", saved.getId(), mapVehicle(saved)));
    }

    @GetMapping("/personas/{id}")
    public ResponseEntity<Map<String, Object>> consultarPersona(@PathVariable int id) {
        Persona persona = personaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada: " + id));
        return ResponseEntity.ok(mapPersona(persona));
    }

    @GetMapping("/vehicles/{id}")
    public ResponseEntity<Map<String, Object>> consultarVehiculo(@PathVariable long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Vehiculo no encontrado: " + id));
        return ResponseEntity.ok(mapVehicle(vehicle));
    }

    @GetMapping("/puntos/{id}")
    public ResponseEntity<Map<String, Object>> consultarPunto(@PathVariable int id) {
        Coordenadas coordenada = coordenadasRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Punto no encontrado: " + id));
        return ResponseEntity.ok(mapCoordenada(coordenada));
    }

    @GetMapping("/links")
    public Map<String, Object> links() {
        Map<String, Object> links = new LinkedHashMap<>();
        links.put("personas", localUrl("/personas"));
        links.put("vehiculos", localUrl("/vehicles"));
        links.put("coordenadas", localUrl("/LaboratorioV1/coordenadas"));
        links.put("cargueDemo", localUrl("/api/excel/cargue/2026053101"));
        return links;
    }

    private int nextPersonaId() {
        Number value = (Number) entityManager
            .createNativeQuery("SELECT COALESCE(MAX(id), 0) + 1 FROM persona")
            .getSingleResult();
        return value.intValue();
    }

    private Map<String, Object> withJsonLink(String type, Object id, Map<String, Object> data) {
        data.put("jsonLink", localUrl("/api/demo/" + ("vehicle".equals(type) ? "vehicles" : "personas") + "/" + id));
        return data;
    }

    private Map<String, Object> mapPersona(Persona persona) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", persona.getId());
        map.put("pnombre", persona.getPnombre());
        map.put("edad", persona.getEdad());
        map.put("ubicacion", persona.getUbicacion());
        map.put("tipoIdentificacion", persona.getTipoIdentificacion());
        map.put("numeroIdentificacion", persona.getNumeroIdentificacion());
        map.put("tipoPersona", persona.getTipoPersona());
        map.put("fechaLicencia", persona.getFechaLicencia());
        return map;
    }

    private Map<String, Object> mapVehicle(Vehicle vehicle) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", vehicle.getId());
        map.put("vehicleType", vehicle.getVehicleType());
        map.put("licensePlate", vehicle.getLicensePlate());
        map.put("serviceType", vehicle.getServiceType());
        map.put("fuelType", vehicle.getFuelType());
        map.put("passengerCapacity", vehicle.getPassengerCapacity());
        map.put("color", vehicle.getColor());
        map.put("modelYear", vehicle.getModelYear());
        map.put("brand", vehicle.getBrand());
        map.put("line", vehicle.getLine());
        map.put("jsonLink", localUrl("/api/demo/vehicles/" + vehicle.getId()));
        return map;
    }

    private Map<String, Object> mapCoordenada(Coordenadas coordenada) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", coordenada.getId());
        map.put("persona", coordenada.getPersona());
        map.put("marca", coordenada.getMarca());
        map.put("latitud", coordenada.getLatitud());
        map.put("longitud", coordenada.getLongitud());
        map.put("jsonLink", localUrl("/api/demo/puntos/" + coordenada.getId()));
        return map;
    }

    private String localUrl(@NonNull String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path(path)
            .toUriString();
    }

    private static String required(Map<String, Object> body, String key) {
        String value = stringValue(body.get(key));
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El campo " + key + " es obligatorio");
        }
        return value.trim();
    }

    private static String valueOrDefault(Map<String, Object> body, String key, String defaultValue) {
        String value = stringValue(body.get(key));
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private static String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static double doubleValue(Object value) {
        if (value == null || String.valueOf(value).isBlank()) {
            throw new IllegalArgumentException("Debe enviar latitud y longitud para dibujar el punto en el mapa");
        }
        return Double.parseDouble(String.valueOf(value));
    }
}
