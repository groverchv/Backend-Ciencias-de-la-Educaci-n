package com.uagrm.ciencias_de_la_educacion.Service;

import com.uagrm.ciencias_de_la_educacion.DTO.ContenidoListDTO;
import com.uagrm.ciencias_de_la_educacion.Model.ContenidoEntity;
import com.uagrm.ciencias_de_la_educacion.Model.Sub_MenuEntity;
import com.uagrm.ciencias_de_la_educacion.Model.UsuarioEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.ContenidoRepository;
import com.uagrm.ciencias_de_la_educacion.Repository.Sub_MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ContenidoService {

    @Autowired
    private ContenidoRepository contenidoRepository;

    @Autowired
    private Sub_MenuRepository subMenuRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Obtener todos los contenidos del sistema
     */
    @Transactional(readOnly = true)
    public List<ContenidoListDTO> getAllContenidos() {
        List<ContenidoEntity> contenidos = contenidoRepository.findAll();

        return contenidos.stream().map(c -> {
            ContenidoListDTO dto = new ContenidoListDTO();
            dto.setId(c.getId());
            dto.setTitulo(c.getTitulo());
            dto.setOrden(c.getOrden());
            dto.setEstado(c.getEstado());
            if (c.getSubMenu() != null) {
                dto.setSubMenuId(c.getSubMenu().getId());
            }
            dto.setBloqueCount(0); // BloqueContenido was removed
            return dto;
        }).collect(Collectors.toList());
    }

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
     * contenidoPublicado y envía notificación WebSocket
     */
    @Transactional
    public ContenidoEntity updateContenido(Long id, String titulo, Boolean estado, String contenidoHtml) {
        ContenidoEntity contenido = getContenidoById(id);
        boolean wasPublished = contenido.getEstado() != null && contenido.getEstado();

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
                
                // Enviar notificación WebSocket solo si es una nueva publicación (no si ya estaba publicado)
                if (!wasPublished) {
                    enviarNotificacionPublicacion(contenido);
                }
            }
        }

        return contenidoRepository.save(contenido);
    }
    
    /**
     * Enviar notificación WebSocket cuando se publica nuevo contenido
     */
    private void enviarNotificacionPublicacion(ContenidoEntity contenido) {
        try {
            Map<String, Object> notificacion = new HashMap<>();
            notificacion.put("contenidoId", contenido.getId());
            notificacion.put("titulo", contenido.getTitulo());
            
            if (contenido.getSubMenu() != null) {
                notificacion.put("subMenuTitulo", contenido.getSubMenu().getTitulo());
                notificacion.put("ruta", contenido.getSubMenu().getRuta());
                
                if (contenido.getSubMenu().getMenu_id() != null) {
                    notificacion.put("menuTitulo", contenido.getSubMenu().getMenu_id().getTitulo());
                }
            }
            
            // Enviar notificación a todos los usuarios conectados
            messagingTemplate.convertAndSend("/topic/contenido-publicado", notificacion);
            System.out.println("📢 Notificación de publicación enviada: " + contenido.getTitulo());
        } catch (Exception e) {
            System.err.println("❌ Error al enviar notificación de publicación: " + e.getMessage());
        }
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
