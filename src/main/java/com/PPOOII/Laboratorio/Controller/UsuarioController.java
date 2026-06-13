package com.PPOOII.Laboratorio.Controller;

import com.PPOOII.Laboratorio.Entities.Usuario;
import com.PPOOII.Laboratorio.Services.LaboratorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final LaboratorioService laboratorioService;

    public UsuarioController(LaboratorioService laboratorioService) {
        this.laboratorioService = laboratorioService;
    }

    @PutMapping("/{login}/password")
    public ResponseEntity<Map<String, Object>> cambiarPassword(
        @PathVariable String login,
        @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.ok(mapUsuario(laboratorioService.cambiarPassword(login, body.get("password"))));
    }

    @GetMapping("/{login}/apikey/regenerar")
    public ResponseEntity<Map<String, Object>> regenerarApiKey(@PathVariable String login) {
        return ResponseEntity.ok(mapUsuario(laboratorioService.regenerarApiKey(login)));
    }

    private Map<String, Object> mapUsuario(Usuario usuario) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", usuario.getId());
        map.put("login", usuario.getLogin());
        map.put("personaId", usuario.getPersona().getId());
        map.put("apikey", usuario.getApikey());
        return map;
    }
}
