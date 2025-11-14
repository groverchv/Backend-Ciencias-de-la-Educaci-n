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
    private Integer orden;
    private Boolean estado = true;


    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "sub_menu_id")
    private Sub_MenuEntity sub_menu_id;

    @ManyToOne
    @JoinColumn(name = "titulo_id")
    private TituloEntity titulo_id;

    @ManyToOne
    @JoinColumn(name = "archivo_id")
    private ArchivoEntity archivo_id;
    
    @ManyToOne
    @JoinColumn(name = "sub_titulo_id")
    private Sub_TituloEntity sub_titulo_id; 

    @ManyToOne
    @JoinColumn(name = "texto_id")
    private TextoEntity texto_id;


}

