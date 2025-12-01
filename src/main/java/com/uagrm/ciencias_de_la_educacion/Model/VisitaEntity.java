package com.uagrm.ciencias_de_la_educacion.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenido_id", nullable = false)
    private ContenidoEntity contenido;

    @Column(name = "fecha_visita", nullable = false)
    private LocalDateTime fechaVisita;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @PrePersist
    protected void onCreate() {
        fechaVisita = LocalDateTime.now();
    }
}
