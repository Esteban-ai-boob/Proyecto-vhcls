package com.PPOOII.Laboratorio.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.time.LocalDate;

@Entity(name = "PER")
@Check(constraints = "tipo_identificacion in ('CC', 'CE', 'TI', 'PAS') and tipo_persona in ('ADMINISTRATIVO', 'CONDUCTOR', 'CLIENTE')")
@Table(name = "persona", schema = "ppooii")
public class Persona implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "pnombre", length = 100, nullable = false)
    private String pnombre;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "ubicacion", length = 255)
    private String ubicacion;

    @Column(name = "Edad")
    private Integer edad;

    @Column(name = "tipo_identificacion", length = 3)
    private String tipoIdentificacion = "CC";

    @Column(name = "numero_identificacion", length = 30, unique = true)
    private String numeroIdentificacion;

    @Column(name = "tipo_persona", length = 20)
    private String tipoPersona = "CLIENTE";

    @Column(name = "fecha_licencia")
    private LocalDate fechaLicencia;

    @Lob
    @JsonIgnore
    @Column(name = "licencia_blob", columnDefinition = "LONGBLOB")
    private byte[] licenciaBlob;

    public Persona() {
    }

    public Persona(int id, String pnombre, String ubicacion) {
        this.id = id;
        this.pnombre = pnombre;
        this.ubicacion = ubicacion;
    }

    @PrePersist
    @PreUpdate
    public void normalizeAndValidate() {
        if (tipoIdentificacion == null || tipoIdentificacion.isBlank()) {
            tipoIdentificacion = "CC";
        }
        tipoIdentificacion = tipoIdentificacion.trim().toUpperCase();
        if (!tipoIdentificacion.matches("^(CC|CE|TI|PAS)$")) {
            throw new IllegalArgumentException("Tipo de identificacion invalido. Use CC, CE, TI o PAS");
        }

        if (tipoPersona == null || tipoPersona.isBlank()) {
            tipoPersona = "CLIENTE";
        }
        tipoPersona = tipoPersona.trim().toUpperCase();
        if (!tipoPersona.matches("^(ADMINISTRATIVO|CONDUCTOR|CLIENTE)$")) {
            throw new IllegalArgumentException("Tipo de persona invalido. Use ADMINISTRATIVO, CONDUCTOR o CLIENTE");
        }

        if (numeroIdentificacion != null) {
            numeroIdentificacion = numeroIdentificacion.trim();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPnombre() {
        return pnombre;
    }

    public void setPnombre(String pnombre) {
        this.pnombre = pnombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public LocalDate getFechaLicencia() {
        return fechaLicencia;
    }

    public void setFechaLicencia(LocalDate fechaLicencia) {
        this.fechaLicencia = fechaLicencia;
    }

    public byte[] getLicenciaBlob() {
        return licenciaBlob;
    }

    public void setLicenciaBlob(byte[] licenciaBlob) {
        this.licenciaBlob = licenciaBlob;
    }
}
