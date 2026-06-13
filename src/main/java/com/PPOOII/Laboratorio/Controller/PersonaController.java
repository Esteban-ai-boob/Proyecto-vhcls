package com.PPOOII.Laboratorio.Controller;

import com.PPOOII.Laboratorio.Entities.Persona;
import com.PPOOII.Laboratorio.Repository.IPersonaRepository;
import com.PPOOII.Laboratorio.Services.LaboratorioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "3. Gestión de Personas", description = "Operaciones CRUD para Personas (Duplicados de Laboratorio V1)")
@RestController
@RequestMapping("/personas")
public class PersonaController {

    private final IPersonaRepository personaRepository;
    private final LaboratorioService laboratorioService;

    public PersonaController(IPersonaRepository personaRepository, LaboratorioService laboratorioService) {
        this.personaRepository = personaRepository;
        this.laboratorioService = laboratorioService;
    }

    @PostMapping
    public Persona crearPersona(@RequestBody Map<String, Object> body) {
        return laboratorioService.crearPersona(mapPersona(new Persona(), body), stringValue(body.get("licenciaBase64")));
    }

    @GetMapping
    public List<Persona> listarPersonas() {
        return personaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Persona consultarPersona(@PathVariable int id) {
        return personaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada: " + id));
    }

    @PutMapping("/{id}")
    public Persona actualizarPersona(@PathVariable int id, @RequestBody Map<String, Object> body) {
        return laboratorioService.actualizarPersona(id, mapPersona(new Persona(), body), stringValue(body.get("licenciaBase64")));
    }

    private @NonNull Persona mapPersona(@NonNull Persona persona, Map<String, Object> body) {
        if (body.get("id") != null) {
            persona.setId(intValue(body.get("id")));
        }
        persona.setPnombre(valueOrDefault(body, "nombre", stringValue(body.get("pnombre"))));
        persona.setUbicacion(stringValue(body.get("ubicacion")));
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
}
