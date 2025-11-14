package com.uagrm.ciencias_de_la_educacion.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private Long id;
    private String correo;
    private String nombre;
    private String apellido;
    private List<String> roles;

    public JwtResponse(String token, String refreshToken, Long id, String correo, String nombre, String apellido, List<String> roles) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.id = id;
        this.correo = correo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.roles = roles;
    }
}
