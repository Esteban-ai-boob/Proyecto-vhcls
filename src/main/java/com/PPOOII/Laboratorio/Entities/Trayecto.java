package com.PPOOII.Laboratorio.Entities;

import com.vehiclemanagement.entity.Vehicle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Schema(description = "Parada secuencial de una ruta operada por un vehiculo y un conductor habilitado")
@Table(name = "trayecto")
public class Trayecto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Schema(description = "Codigo agrupador de la ruta", example = "RUTA-IBG-01")
    @Column(name = "codigo_ruta", nullable = false, length = 50)
    private String codigoRuta;

    @Schema(description = "Orden secuencial de la parada dentro de la ruta", example = "1")
    @Column(name = "orden", nullable = false)
    private int orden;

    @Schema(description = "Nombre visible de la parada para la interfaz web", example = "Universidad de Ibague")
    @Column(name = "nombre_parada", nullable = false, length = 120)
    private String nombreParada;

    @Schema(description = "Direccion usada para obtener coordenadas con Google Maps")
    @Column(name = "ubicacion", nullable = false, length = 255)
    private String ubicacion;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Column(name = "vehiculo_id", nullable = false)
    private Long vehiculoId;

    public Long getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(Long vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehiculo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conductor_id", nullable = false)
    private Persona conductor;

    @PrePersist
    @PreUpdate
    public void normalize() {
        if (codigoRuta != null) {
            codigoRuta = codigoRuta.trim().toUpperCase();
        }
        if (vehiculo != null) {
            vehiculoId = vehiculo.getId();
        }
    }

    public Long getId() {
        return id;
    }

    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public String getNombreParada() {
        return nombreParada;
    }

    public void setNombreParada(String nombreParada) {
        this.nombreParada = nombreParada;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Vehicle getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehicle vehiculo) {
        this.vehiculo = vehiculo;
        this.vehiculoId = vehiculo == null ? null : vehiculo.getId();
    }

    public Persona getConductor() {
        return conductor;
    }

    public void setConductor(Persona conductor) {
        this.conductor = conductor;
    }
}
