package com.uagrm.ciencias_de_la_educacion.DTO;

import java.util.Map;

// Representa un bloque individual del frontend
public class BloqueDTO {
    
    // El 'id' es String porque puede venir como "temp_123"
    private String id; 
    private String tipoBloque;
    private Map<String, Object> datosJson;
    private Integer orden;
    private Boolean estado;

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTipoBloque() { return tipoBloque; }
    public void setTipoBloque(String tipoBloque) { this.tipoBloque = tipoBloque; }
    public Map<String, Object> getDatosJson() { return datosJson; }
    public void setDatosJson(Map<String, Object> datosJson) { this.datosJson = datosJson; }
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}