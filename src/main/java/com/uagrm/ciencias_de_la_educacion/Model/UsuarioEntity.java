package com.uagrm.ciencias_de_la_educacion.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "usuario")
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String apellido;
    
    @Column(unique = true, nullable = false)
    private String correo;
    
    @Column(nullable = false)
    private String password;
    
    private Boolean estado=true;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "rol")
    private Set<String> roles = new HashSet<>();
}
