package com.uagrm.ciencias_de_la_educacion.Service;

import com.uagrm.ciencias_de_la_educacion.DTO.ContenidoListDTO;
import com.uagrm.ciencias_de_la_educacion.Model.ContenidoEntity;
import com.uagrm.ciencias_de_la_educacion.Model.Sub_MenuEntity;
import com.uagrm.ciencias_de_la_educacion.Model.UsuarioEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.ContenidoRepository;
import com.uagrm.ciencias_de_la_educacion.Repository.Sub_MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContenidoService {

    @Autowired
    private ContenidoRepository contenidoRepository;

    @Autowired
    private Sub_MenuRepository subMenuRepository;

    /**
     * Obtener todos los contenidos de un SubMenu
     */
    @Transactional(readOnly = true)
    public List<ContenidoListDTO> getContenidosBySubMenu(Long subMenuId) {
        List<ContenidoEntity> contenidos = contenidoRepository.findBySubMenu_IdOrderByOrdenAsc(subMenuId);

        return contenidos.stream().map(c -> {
            ContenidoListDTO dto = new ContenidoListDTO();
            dto.setId(c.getId());
            dto.setTitulo(c.getTitulo());
            dto.setOrden(c.getOrden());
            dto.setEstado(c.getEstado());
            dto.setSubMenuId(c.getSubMenu().getId());
            dto.setBloqueCount(0); // BloqueContenido was removed
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Obtener un contenido por ID con sus bloques
     */
    @Transactional(readOnly = true)
    public ContenidoEntity getContenidoById(Long id) {
        return contenidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contenido no encontrado con id: " + id));
    }

    /**
     * Crear un nuevo contenido
     */
    @Transactional
    public ContenidoEntity createContenido(Long subMenuId, String titulo, UsuarioEntity usuario) {
        Sub_MenuEntity subMenu = subMenuRepository.findById(subMenuId)
                .orElseThrow(() -> new EntityNotFoundException("SubMenu no encontrado con id: " + subMenuId));

        // Calcular el orden (último + 1)
        long count = contenidoRepository.countBySubMenu_Id(subMenuId);

        ContenidoEntity contenido = new ContenidoEntity();
        contenido.setTitulo(titulo);
        contenido.setSubMenu(subMenu);
        contenido.setUsuario(usuario);
        contenido.setOrden((int) count);
        contenido.setEstado(true);

        return contenidoRepository.save(contenido);
    }

    /**
     * Actualizar un contenido (título, estado y contenido HTML)
     * Cuando se publica (estado = true), copia el contenidoHtml a
     * contenidoPublicado
     */
    @Transactional
    public ContenidoEntity updateContenido(Long id, String titulo, Boolean estado, String contenidoHtml) {
        ContenidoEntity contenido = getContenidoById(id);

        if (titulo != null) {
            contenido.setTitulo(titulo);
        }

        if (contenidoHtml != null) {
            contenido.setContenidoHtml(contenidoHtml);
        }

        if (estado != null) {
            contenido.setEstado(estado);

            // Si se está publicando (estado = true), copiar el borrador al contenido
            // publicado
            if (estado == true) {
                contenido.setContenidoPublicado(contenido.getContenidoHtml());
            }
        }

        return contenidoRepository.save(contenido);
    }

    /**
     * Eliminar un contenido (y todos sus bloques en cascada)
     */
    @Transactional
    public void deleteContenido(Long id) {
        ContenidoEntity contenido = getContenidoById(id);
        contenidoRepository.delete(contenido);
    }
}
