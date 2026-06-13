package com.PPOOII.Laboratorio.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Ruta completa agrupada por codigo con paradas secuenciales")
public class RutaDetalleDTO {

    @Schema(example = "RUTA-IBG-01")
    private String codigoRuta;

    @Schema(example = "ABC123")
    private String placa;

    @Schema(example = "Camila")
    private String conductor;

    @Schema(example = "100000002")
    private String identificacionConductor;

    private List<RutaParadaDTO> paradas;

    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getConductor() {
        return conductor;
    }

    public void setConductor(String conductor) {
        this.conductor = conductor;
    }

    public String getIdentificacionConductor() {
        return identificacionConductor;
    }

    public void setIdentificacionConductor(String identificacionConductor) {
        this.identificacionConductor = identificacionConductor;
    }

    public List<RutaParadaDTO> getParadas() {
        return paradas;
    }

    public void setParadas(List<RutaParadaDTO> paradas) {
        this.paradas = paradas;
    }
}
