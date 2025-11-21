package com.uagrm.ciencias_de_la_educacion.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "bloque_contenido")
public class BloqueContenido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer orden;

    @Column(name = "tipo_bloque", nullable = false)
    private String tipoBloque;

    @Column(nullable = false)
    private Boolean estado = true;

    // Mapeo de columna JSONB de PostgreSQL a Map en Java
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "datos_json", columnDefinition = "jsonb")
    private Map<String, Object> datosJson;

    // Relación ManyToOne con ContenidoEntity
    // Ahora los bloques pertenecen a un Contenido, no directamente a SubMenu
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenido_id", nullable = false)
    private ContenidoEntity contenido;

    // Relación parent-child para estructura jerárquica
    // Un bloque puede tener un bloque padre (por ejemplo, un subtítulo)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_bloque_id")
    private BloqueContenido parentBloque;

    // Un bloque puede tener múltiples bloques hijos
    @OneToMany(mappedBy = "parentBloque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BloqueContenido> childBloques = new ArrayList<>();
}
