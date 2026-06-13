package com.PPOOII.Laboratorio.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity(name = "COOR")
@Table(name = "coordenadas", schema = "ppooii")
public class Coordenadas implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_coordenada")
    public int id;

    @Column(name = "persona", nullable = false)
    public int persona;

    @Column(name = "marca", length = 100, nullable = false)
    public String marca;

    @Column(name = "longitud", nullable = false)
    public double longitud;

    @Column(name = "latitud", nullable = false)
    public double latitud;

    public Coordenadas() {
    }

    public Coordenadas(int persona, String marca, double longitud, double latitud) {
        this.persona = persona;
        this.marca = marca;
        this.longitud = longitud;
        this.latitud = latitud;
    }

    public Coordenadas(int id, int persona, String marca, double longitud, double latitud) {
        this.id = id;
        this.persona = persona;
        this.marca = marca;
        this.longitud = longitud;
        this.latitud = latitud;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPersona() {
        return persona;
    }

    public void setPersona(int persona) {
        this.persona = persona;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }
}
