package com.uagrm.ciencias_de_la_educacion.Repository;

import com.uagrm.ciencias_de_la_educacion.Model.ContenidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContenidoRepository extends JpaRepository<ContenidoEntity, Long> {

    // Obtener todos los contenidos de un SubMenu, ordenados
    List<ContenidoEntity> findBySubMenu_IdOrderByOrdenAsc(Long subMenuId);

    // Eliminar todos los contenidos de un SubMenu
    void deleteBySubMenu_Id(Long subMenuId);

    // Contar contenidos de un SubMenu
    long countBySubMenu_Id(Long subMenuId);
}
