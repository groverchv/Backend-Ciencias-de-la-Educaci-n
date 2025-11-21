package com.uagrm.ciencias_de_la_educacion.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContenidoCreateRequestDTO {
    private String titulo;
    private Map<String, Object> usuario_id; // { "id": X }
}
