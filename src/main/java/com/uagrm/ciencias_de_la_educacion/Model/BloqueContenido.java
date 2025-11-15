package com.uagrm.ciencias_de_la_educacion.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    // Relación ManyToOne con Sub_MenuEntity
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_menu_id", nullable = false)
    private Sub_MenuEntity subMenu;
}
