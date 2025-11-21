package com.uagrm.ciencias_de_la_educacion.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContenidoListDTO {
    private Long id;
    private String titulo;
    private Integer orden;
    private Boolean estado;
    private Long subMenuId;
    private Integer bloqueCount; // Número de bloques que tiene
}
