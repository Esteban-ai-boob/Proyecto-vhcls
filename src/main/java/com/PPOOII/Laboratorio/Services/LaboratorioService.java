package com.PPOOII.Laboratorio.Services;

import com.PPOOII.Laboratorio.APIs.GoogleMaps.Geocoder;
import com.PPOOII.Laboratorio.Entities.ConductorVehiculo;
import com.PPOOII.Laboratorio.Entities.ConductorVehiculo.EstadoConductor;
import com.PPOOII.Laboratorio.Entities.Persona;
import com.PPOOII.Laboratorio.Entities.Trayecto;
import com.PPOOII.Laboratorio.Entities.Usuario;
import com.PPOOII.Laboratorio.Repository.ConductorVehiculoRepository;
import com.PPOOII.Laboratorio.Repository.IPersonaRepository;
import com.PPOOII.Laboratorio.Repository.TrayectoRepository;
import com.PPOOII.Laboratorio.Repository.UsuarioRepository;
import com.vehiclemanagement.entity.Vehicle;
import com.vehiclemanagement.entity.VehicleDocument;
import com.vehiclemanagement.repository.VehicleDocumentRepository;
import com.vehiclemanagement.repository.VehicleRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class LaboratorioService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final IPersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final ConductorVehiculoRepository conductorVehiculoRepository;
    private final TrayectoRepository trayectoRepository;
    private final org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;
    private final Geocoder geocoder = new Geocoder();

    public LaboratorioService(
        IPersonaRepository personaRepository,
        UsuarioRepository usuarioRepository,
        VehicleRepository vehicleRepository,
        VehicleDocumentRepository vehicleDocumentRepository,
        ConductorVehiculoRepository conductorVehiculoRepository,
        TrayectoRepository trayectoRepository,
        org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate
    ) {
        this.personaRepository = personaRepository;
        this.usuarioRepository = usuarioRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleDocumentRepository = vehicleDocumentRepository;
        this.conductorVehiculoRepository = conductorVehiculoRepository;
        this.trayectoRepository = trayectoRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Persona crearPersona(@NonNull Persona persona, String licenciaBase64) {
        if (persona.getPnombre() == null || persona.getPnombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (persona.getId() == 0) {
            persona.setId(personaRepository.getNextId());
        }
        if (licenciaBase64 != null && !licenciaBase64.isBlank()) {
            persona.setLicenciaBlob(decodeBase64(licenciaBase64));
        }
        Persona guardada = personaRepository.save(persona);
        
        try {
            rabbitTemplate.convertAndSend("notificaciones_queue", "Nueva persona creada: " + guardada.getPnombre() + " con cédula " + guardada.getNumeroIdentificacion());
        } catch (org.springframework.amqp.AmqpException ignored) { }

        if ("ADMINISTRATIVO".equalsIgnoreCase(guardada.getTipoPersona())) {
            crearUsuarioAutomatico(guardada);
        }
        return guardada;
    }

    public Persona actualizarPersona(int id, Persona persona, String licenciaBase64) {
        Persona existente = personaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada: " + id));
            
        if (persona.getPnombre() != null && !persona.getPnombre().isBlank()) {
            existente.setPnombre(persona.getPnombre());
        }
        if (persona.getUbicacion() != null) existente.setUbicacion(persona.getUbicacion());
        if (persona.getEdad() != null) existente.setEdad(persona.getEdad());
        if (persona.getTipoIdentificacion() != null) existente.setTipoIdentificacion(persona.getTipoIdentificacion());
        if (persona.getNumeroIdentificacion() != null) existente.setNumeroIdentificacion(persona.getNumeroIdentificacion());
        if (persona.getTipoPersona() != null) existente.setTipoPersona(persona.getTipoPersona());
        if (persona.getFechaLicencia() != null) existente.setFechaLicencia(persona.getFechaLicencia());
        
        if (licenciaBase64 != null && !licenciaBase64.isBlank()) {
            existente.setLicenciaBlob(decodeBase64(licenciaBase64));
        }
        
        try {
            @SuppressWarnings("null")
            Persona guardada = personaRepository.save(existente);
            return guardada;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar la persona: " + e.getMessage(), e);
        }
    }

    public Usuario cambiarPassword(String login, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findByUsername(login)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con login " + login));
        usuario.setPassword(nuevaPassword);
        return usuarioRepository.save(usuario);
    }

    public Usuario regenerarApiKey(String login) {
        Usuario usuario = usuarioRepository.findByUsername(login)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con login " + login));
        usuario.setApikey(generarApiKey());
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarPersona(String numeroIdentificacion) {
        Persona persona = personaRepository.findByNumeroIdentificacion(numeroIdentificacion)
            .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada: " + numeroIdentificacion));
        personaRepository.deleteById(persona.getId());
    }

    public ConductorVehiculo asociarConductorVehiculo(int conductorId, long vehiculoId, EstadoConductor estado) {
        Persona conductor = personaRepository.findById(conductorId)
            .orElseThrow(() -> new IllegalArgumentException("Conductor no encontrado: " + conductorId));
        if (!"CONDUCTOR".equalsIgnoreCase(conductor.getTipoPersona())) {
            throw new IllegalArgumentException("La persona debe tener tipo CONDUCTOR");
        }
        Vehicle vehiculo = vehicleRepository.findById(vehiculoId)
            .orElseThrow(() -> new IllegalArgumentException("Vehiculo no encontrado: " + vehiculoId));
        ConductorVehiculo relacion = conductorVehiculoRepository
            .findByConductor_IdAndVehiculo_Id(conductorId, vehiculoId)
            .orElseGet(ConductorVehiculo::new);
        relacion.setConductor(conductor);
        relacion.setVehiculo(vehiculo);
        relacion.setEstado(estado == null ? EstadoConductor.EA : estado);
        return conductorVehiculoRepository.save(relacion);
    }

    public ConductorVehiculo cambiarEstadoConductorVehiculo(int conductorId, long vehiculoId, EstadoConductor estado) {
        ConductorVehiculo relacion = conductorVehiculoRepository
            .findByConductor_IdAndVehiculo_Id(conductorId, vehiculoId)
            .orElseThrow(() -> new IllegalArgumentException("Relacion conductor-vehiculo no encontrada"));
        relacion.setEstado(estado);
        return conductorVehiculoRepository.save(relacion);
    }

    @Transactional
    public Trayecto crearTrayecto(Trayecto trayecto, long vehiculoId, int conductorId) {
        ConductorVehiculo relacion = conductorVehiculoRepository
            .findByConductor_IdAndVehiculo_Id(conductorId, vehiculoId)
            .orElseThrow(() -> new IllegalArgumentException("Debe asociar el conductor con el vehiculo antes de crear trayecto"));
        if (relacion.getEstado() != EstadoConductor.PO) {
            throw new IllegalArgumentException("El conductor debe estar en estado PO para operar el vehiculo");
        }
        boolean documentosHabilitados = vehicleDocumentRepository.findByVehicleId(vehiculoId).stream()
            .allMatch(vd -> vd.getDocumentStatus() == VehicleDocument.DocumentStatus.HABILITADO);
        if (!documentosHabilitados) {
            throw new IllegalArgumentException("Todos los documentos del vehiculo deben estar HABILITADOS");
        }
        trayecto.setVehiculo(relacion.getVehiculo());
        trayecto.setVehiculoId(vehiculoId);
        trayecto.setConductor(relacion.getConductor());
        completarCoordenadas(trayecto);
        return trayectoRepository.save(trayecto);
    }

    @Transactional
    public Map<String, Object> crearYMapearTrayecto(Trayecto trayecto, long vehiculoId, int conductorId) {
        return mapTrayecto(crearTrayecto(trayecto, vehiculoId, conductorId));
    }

    @Transactional(readOnly = true)
    public List<Trayecto> consultarRuta(String codigoRuta) {
        return trayectoRepository.findByCodigoRutaOrder(codigoRuta.trim().toUpperCase(Locale.ROOT));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> consultarRutaMapeada(String codigoRuta) {
        return consultarRuta(codigoRuta).stream().map(LaboratorioService::mapTrayecto).toList();
    }

    @Transactional(readOnly = true)
    public List<String> consultarCodigosRutaPorConductor(String numeroIdentificacion) {
        return trayectoRepository.findCodigosRutaByConductor(numeroIdentificacion);
    }

    @Transactional(readOnly = true)
    public Map<String, List<Map<String, Object>>> consultarRutasPorPlaca(String placa) {
        Map<String, List<Map<String, Object>>> agrupado = new LinkedHashMap<>();
        for (Trayecto trayecto : trayectoRepository.findByPlaca(placa)) {
            agrupado.computeIfAbsent(trayecto.getCodigoRuta(), ignored -> new java.util.ArrayList<>())
                .add(mapTrayecto(trayecto));
        }
        return agrupado;
    }

    @Transactional(readOnly = true)
    public List<Trayecto> consultarRutasConBloqueos() {
        return trayectoRepository.findRutasConBloqueos();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> consultarRutasConBloqueosMapeadas() {
        return consultarRutasConBloqueos().stream().map(LaboratorioService::mapTrayecto).toList();
    }

    @Transactional(readOnly = true)
    public List<ConductorVehiculo> consultarConductoresQuePuedenOperar() {
        return conductorVehiculoRepository.findByEstado(EstadoConductor.PO);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> totalPersonasPorTipo() {
        Map<String, Long> resultado = new LinkedHashMap<>();
        for (Object[] fila : personaRepository.countPersonasByTipo()) {
            resultado.put((String) fila[0], (Long) fila[1]);
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public Integer obtenerSiguienteOrdenRuta(String codigoRuta) {
        return trayectoRepository.findMaxOrdenByCodigoRuta(codigoRuta) + 1;
    }


    @Transactional
    public void eliminarTrayecto(@NonNull Long id) {
        if (!trayectoRepository.existsById(id)) {
            throw new IllegalArgumentException("El trayecto con id " + id + " no existe.");
        }
        trayectoRepository.deleteById(id);
    }

    public void completarCoordenadas(Trayecto trayecto) {
        if (trayecto.getLatitud() != null && trayecto.getLongitud() != null) {
            return;
        }
        try {
            if (geocoder.isConfigured()) {
                String resultado = geocoder.getLating(trayecto.getUbicacion());
                if (resultado != null && !resultado.isBlank()) {
                    String[] partes = resultado.split(",");
                    trayecto.setLatitud(Double.valueOf(partes[0].trim()));
                    trayecto.setLongitud(Double.valueOf(partes[1].trim()));
                    return;
                }
            }
        } catch (NumberFormatException | IllegalStateException | java.io.IOException | InterruptedException ignored) {
        }
        
        // Fallback: usar Nominatim de OpenStreetMap (Gratuito)
        try {
            String query = java.net.URLEncoder.encode(trayecto.getUbicacion() + ", Colombia", "UTF-8");
            String urlStr = "https://nominatim.openstreetmap.org/search?q=" + query + "&format=json&limit=1";
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) java.net.URI.create(urlStr).toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "LaboratorioApp/1.0");
            
            if (conn.getResponseCode() == 200) {
                try (java.util.Scanner scanner = new java.util.Scanner(conn.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                
                    if (response.contains("\"lat\"") && response.contains("\"lon\"")) {
                        String latStr = response.split("\"lat\":\"")[1].split("\"")[0];
                        String lonStr = response.split("\"lon\":\"")[1].split("\"")[0];
                        trayecto.setLatitud(Double.valueOf(latStr));
                        trayecto.setLongitud(Double.valueOf(lonStr));
                        return;
                    }
                }
            }
        } catch (java.io.IOException | NumberFormatException e) {
            System.err.println("Nominatim fallback failed: " + e.getMessage());
        }

        // Coordenadas de respaldo si todo falla
        double offset = Math.max(0, trayecto.getOrden() - 1) * 0.004;
        trayecto.setLatitud(4.4389 + offset);
        trayecto.setLongitud(-75.2322 - offset);
    }

    public static Map<String, Object> mapTrayecto(Trayecto trayecto) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", trayecto.getId());
        map.put("codigoRuta", trayecto.getCodigoRuta());
        map.put("orden", trayecto.getOrden());
        map.put("nombreParada", trayecto.getNombreParada());
        map.put("ubicacion", trayecto.getUbicacion());
        map.put("latitud", trayecto.getLatitud());
        map.put("longitud", trayecto.getLongitud());
        map.put("placa", trayecto.getVehiculo().getLicensePlate());
        map.put("conductor", trayecto.getConductor().getPnombre());
        map.put("identificacionConductor", trayecto.getConductor().getNumeroIdentificacion());
        return map;
    }

    private void crearUsuarioAutomatico(Persona persona) {
        String login = generarLogin(persona);
        if (usuarioRepository.findByUsername(login).isPresent()) {
            return;
        }
        usuarioRepository.save(new Usuario(login, generarPassword(), generarApiKey(), persona));
    }

    private String generarLogin(Persona persona) {
        String nombre = persona.getPnombre() == null ? "usuario" : persona.getPnombre();
        String limpio = nombre.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        if (limpio.isBlank()) {
            limpio = "usuario";
        }
        return limpio.substring(0, Math.min(12, limpio.length())) + persona.getId();
    }

    private String generarPassword() {
        return "Pwd" + (100000 + RANDOM.nextInt(900000));
    }

    private String generarApiKey() {
        return "APIKEY-" + UUID.randomUUID();
    }

    private byte[] decodeBase64(String value) {
        try {
            return Base64.getDecoder().decode(value);
        } catch (IllegalArgumentException ex) {
            return value.getBytes(StandardCharsets.UTF_8);
        }
    }

    public LocalDate hoy() {
        return LocalDate.now();
    }
}
