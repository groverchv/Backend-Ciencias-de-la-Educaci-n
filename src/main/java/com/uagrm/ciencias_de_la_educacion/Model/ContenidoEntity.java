package com.uagrm.ciencias_de_la_educacion.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "contenido")
public class ContenidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private Integer orden = 0;

    @Column(nullable = false)
    private Boolean estado = true;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Campo para contenido de texto enriquecido (editor tipo Word) - BORRADOR
    @Lob
    @Column(name = "contenido_html", columnDefinition = "TEXT")
    private String contenidoHtml;

    // Campo para contenido publicado (visible al público)
    @Lob
    @Column(name = "contenido_publicado", columnDefinition = "TEXT")
    private String contenidoPublicado;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Relación con SubMenu (muchos contenidos por submenu)
    @ManyToOne
    @JoinColumn(name = "sub_menu_id", nullable = false)
    private Sub_MenuEntity subMenu;

    // Usuario que creó el contenido
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;
}
