package com.PPOOII.Laboratorio.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Parada ordenada de una ruta para renderizado secuencial en front-end")
public class RutaParadaDTO {

    @Schema(example = "1")
    private int orden;

    @Schema(example = "Universidad de Ibague")
    private String nombreParada;

    @Schema(example = "Universidad de Ibague, Ibague, Tolima, Colombia")
    private String ubicacion;

    @Schema(example = "4.4284")
    private Double latitud;

    @Schema(example = "-75.2138")
    private Double longitud;

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
}
