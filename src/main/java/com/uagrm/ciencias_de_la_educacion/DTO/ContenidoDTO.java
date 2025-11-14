package com.uagrm.ciencias_de_la_educacion.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContenidoDTO {
    private Long id;
    private Long menuId;
    private String menuNombre;
    private String menuRuta;
    private String titulo;
    private String descripcion;
    private List<Map<String, Object>> bloques;
    private Map<String, Object> estilosGlobales;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String creadoPor;
    private String actualizadoPor;
}
