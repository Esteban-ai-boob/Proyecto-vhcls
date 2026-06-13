package com.PPOOII.Laboratorio.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.PPOOII.Laboratorio.APIs.GoogleMaps.Geocoder;
import com.PPOOII.Laboratorio.Entities.ConductorVehiculo;
import com.PPOOII.Laboratorio.Entities.Coordenadas;
import com.PPOOII.Laboratorio.Entities.Persona;
import com.PPOOII.Laboratorio.Entities.Trayecto;
import com.PPOOII.Laboratorio.Repository.ConductorVehiculoRepository;
import com.PPOOII.Laboratorio.Repository.CoordenadasRepository;
import com.PPOOII.Laboratorio.Repository.IPersonaRepository;
import com.PPOOII.Laboratorio.Repository.TrayectoRepository;
import com.PPOOII.Laboratorio.Services.LaboratorioService;
import com.vehiclemanagement.entity.VehicleDocument;
import com.vehiclemanagement.repository.VehicleDocumentRepository;

@Component
public class ScheduledTasks {

    private static final Logger Logger = LogManager.getLogger(ScheduledTasks.class);
    private static final String[] UBICACIONES_SIMULADAS = {
        "Ibague, Tolima, Colombia",
        "Bogota, Colombia",
        "Medellin, Colombia",
        "Cali, Colombia",
        "Bangalore, India"
    };

    private final Geocoder geocoder = new Geocoder();
    private volatile boolean geocoderWarningShown;
    private volatile boolean geocoderBlockedWarningShown;

    private final IPersonaRepository personaRepository;
    private final CoordenadasRepository coordenadasRepository;
    private final ConductorVehiculoRepository conductorVehiculoRepository;
    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final TrayectoRepository trayectoRepository;
    private final LaboratorioService laboratorioService;


    public ScheduledTasks(
            @Qualifier("IPersonaRepository") IPersonaRepository personaRepository,
            @Qualifier("ICoordenadasRepository") CoordenadasRepository coordenadasRepository,
            ConductorVehiculoRepository conductorVehiculoRepository,
            VehicleDocumentRepository vehicleDocumentRepository,
            TrayectoRepository trayectoRepository,
            LaboratorioService laboratorioService) {
        this.personaRepository = personaRepository;
        this.coordenadasRepository = coordenadasRepository;
        this.conductorVehiculoRepository = conductorVehiculoRepository;
        this.vehicleDocumentRepository = vehicleDocumentRepository;
        this.trayectoRepository = trayectoRepository;
        this.laboratorioService = laboratorioService;
    }

    @Scheduled(cron = "*/30 * * * * ?")
    public void actualizarCoordenadas() {
        try {
            if (!geocoder.isConfigured()) {
                if (!geocoderWarningShown) {
                    Logger.warn("Geocoder deshabilitado: configura la variable GOOGLE_MAPS_API_KEY para actualizar coordenadas desde Google Maps");
                    geocoderWarningShown = true;
                }
                return;
            }

            List<Persona> personas = personaRepository.getPersonas();
            for (Persona persona : personas) {
                try {
                    String resultado = geocoder.getLating(persona.getUbicacion());
                    if (resultado == null || resultado.isBlank()) {
                        Logger.warn("No se pudo geocodificar la ubicacion de la persona {}", persona.getId());
                        continue;
                    }

                    String[] coor = resultado.split(",");
                    if (coor.length < 2) {
                        Logger.warn("Respuesta invalida del geocoder para la persona {}: {}", persona.getId(), resultado);
                        continue;
                    }

                    Coordenadas coordenada = coordenadasRepository.getCoordenadaXPersona(persona.getId());
                    if (coordenada == null) {
                        coordenadasRepository.save(
                            new Coordenadas(
                                persona.getId(),
                                persona.getPnombre(),
                                Double.parseDouble(coor[1].trim()),
                                Double.parseDouble(coor[0].trim())
                            )
                        );
                    } else {
                        coordenada.setLongitud(Double.parseDouble(coor[1].trim()));
                        coordenada.setLatitud(Double.parseDouble(coor[0].trim()));
                        coordenada.setMarca(persona.getPnombre());
                        coordenadasRepository.save(coordenada);
                    }
                } catch (IllegalStateException ex) {
                    if (!geocoderBlockedWarningShown) {
                        Logger.warn("Google Geocoding no esta operativo para este proyecto: {}", ex.getMessage());
                        geocoderBlockedWarningShown = true;
                    }
                    return;
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException | java.io.IOException | InterruptedException ex) {
                    Logger.error("Error actualizando coordenadas para la persona {}", persona.getId(), ex);
                }
            }
        } catch (RuntimeException ex) {
            Logger.error("Error ejecutando la tarea principal de coordenadas", ex);
        }
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void simularMovimientoAleatorio() {
        try {
            List<Persona> personas = personaRepository.getPersonas();
            for (Persona persona : personas) {
                String nuevaUbicacion =
                    UBICACIONES_SIMULADAS[ThreadLocalRandom.current().nextInt(UBICACIONES_SIMULADAS.length)];
                persona.setUbicacion(nuevaUbicacion);
                personaRepository.save(persona);
            }
            Logger.info("Simulacion de movimiento aleatorio ejecutada para {} personas", personas.size());
        } catch (Exception ex) {
            Logger.error("Error simulando movimiento aleatorio", ex);
        }
    }

    @Scheduled(cron = "0 */2 * * * ?")
    public void restringirConductoresConLicenciaVencida() {
        try {
            List<Persona> conductores = personaRepository.findConductoresConLicenciaVencida();
            int actualizados = 0;
            for (Persona conductor : conductores) {
                for (ConductorVehiculo relacion : conductorVehiculoRepository.findByConductor_Id(conductor.getId())) {
                    if (relacion.getEstado() != ConductorVehiculo.EstadoConductor.RO) {
                        relacion.setEstado(ConductorVehiculo.EstadoConductor.RO);
                        conductorVehiculoRepository.save(relacion);
                        actualizados++;
                    }
                }
            }
            Logger.info("Licencias vencidas revisadas. Relaciones restringidas: {}", actualizados);
        } catch (Exception ex) {
            Logger.error("Error verificando licencias vencidas", ex);
        }
    }

    @Scheduled(cron = "0 */2 * * * ?")
    public void marcarDocumentosVehiculoVencidos() {
        try {
            List<VehicleDocument> vencidos = vehicleDocumentRepository.findVencidosSinMarcar();
            for (VehicleDocument documento : vencidos) {
                documento.setDocumentStatus(VehicleDocument.DocumentStatus.VENDIDO_VENCIDO);
                vehicleDocumentRepository.save(documento);
            }
            Logger.info("Documentos de vehiculo vencidos marcados: {}", vencidos.size());
        } catch (Exception ex) {
            Logger.error("Error verificando documentos vencidos", ex);
        }
    }

    @Scheduled(fixedRate = 90000)
    public void completarCoordenadasTrayectos() {
        try {
            List<Trayecto> sinCoordenadas = trayectoRepository.findSinCoordenadas();
            for (Trayecto trayecto : sinCoordenadas) {
                if (trayecto == null) {
                    continue;
                }
                laboratorioService.completarCoordenadas(trayecto);
                trayectoRepository.save(trayecto);
            }
            Logger.info("Trayectos sin coordenadas procesados: {}", sinCoordenadas.size());
        } catch (Exception ex) {
            Logger.error("Error completando coordenadas de trayectos", ex);
        }
    }

    @Scheduled(cron = "0 */2 * * * ?")
    public void cronMultiFrecuencia() {
        Logger.info("Ejecucion del cron multifrecuencia de laboratorio 3");
    }
}
