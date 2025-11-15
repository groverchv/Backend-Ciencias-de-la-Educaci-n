package com.uagrm.ciencias_de_la_educacion.DTO;

// DTO simple para recibir solo el ID del usuario
public class UsuarioIdDto {
    private Long id;

    public UsuarioIdDto() {}

    public UsuarioIdDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
