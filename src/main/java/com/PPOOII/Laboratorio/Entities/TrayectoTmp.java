package com.PPOOII.Laboratorio.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "trayecto_tmp", schema = "ppooii")
public class TrayectoTmp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NOMBRE", length = 100)
    private String nombre;

    @Column(name = "EDAD", length = 3)
    private String edad;

    @Column(name = "UBICACION", length = 100)
    private String ubicacion;

    @Column(name = "ESTADO", length = 10, nullable = false)
    private String estado;

    @Column(name = "OBSERVACION", length = 500)
    private String observacion;

    @Column(name = "ID_CARGUE")
    private Double idCargue;

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEdad() {
        return edad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getEstado() {
        return estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public Double getIdCargue() {
        return idCargue;
    }
}
