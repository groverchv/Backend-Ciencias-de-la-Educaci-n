package com.uagrm.ciencias_de_la_educacion.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uagrm.ciencias_de_la_educacion.Model.BloqueContenido;

import java.util.List;

@Repository
public interface BloqueContenidoRepository extends JpaRepository<BloqueContenido, Long> {

    /**
     * Devuelve todos los bloques de un SubMenu, ordenados por 'orden'.
     * Como el campo en BloqueContenido es "subMenu" (que apunta a Sub_MenuEntity),
     * Spring Data JPA usa subMenu_Id para acceder al ID del SubMenu
     */
    List<BloqueContenido> findBySubMenu_IdOrderByOrdenAsc(Long subMenuId);

    /**
     * Borra todos los bloques asociados a un SubMenu.
     * Esto se usará dentro de una transacción.
     */
    void deleteBySubMenu_Id(Long subMenuId);
}
