package com.uagrm.ciencias_de_la_educacion.DTO;

import java.util.List;

// Representa el body completo del POST /sub_menu/{id}/bloques
public class BloqueSaveRequestDTO {
    
    private UsuarioIdDto usuario_id; // Coincide con tu BloqueService.js
    private List<BloqueDTO> bloques;

    // Getters y Setters
    public UsuarioIdDto getUsuario_id() { return usuario_id; }
    public void setUsuario_id(UsuarioIdDto usuario_id) { this.usuario_id = usuario_id; }
    public List<BloqueDTO> getBloques() { return bloques; }
    public void setBloques(List<BloqueDTO> bloques) { this.bloques = bloques; }
}