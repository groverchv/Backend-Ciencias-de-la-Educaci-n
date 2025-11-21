package com.uagrm.ciencias_de_la_educacion.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uagrm.ciencias_de_la_educacion.Model.BloqueContenido;

import java.util.List;

@Repository
public interface BloqueContenidoRepository extends JpaRepository<BloqueContenido, Long> {

    // Buscar bloques por contenido_id ordenados por orden
    List<BloqueContenido> findByContenido_IdOrderByOrdenAsc(Long contenidoId);

    // Eliminar todos los bloques de un contenido
    void deleteByContenido_Id(Long contenidoId);
}
