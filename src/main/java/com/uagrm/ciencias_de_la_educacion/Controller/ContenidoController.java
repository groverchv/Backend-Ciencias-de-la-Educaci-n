package com.uagrm.ciencias_de_la_educacion.Controller;

import com.uagrm.ciencias_de_la_educacion.DTO.ContenidoCreateRequestDTO;
import com.uagrm.ciencias_de_la_educacion.DTO.ContenidoListDTO;
import com.uagrm.ciencias_de_la_educacion.Model.ContenidoEntity;
import com.uagrm.ciencias_de_la_educacion.Model.UsuarioEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.UsuarioRepository;
import com.uagrm.ciencias_de_la_educacion.Service.ContenidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ContenidoController {

    @Autowired
    private ContenidoService contenidoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * GET /api/contenidos
     * Obtiene todos los contenidos del sistema
     */
    @GetMapping("/contenidos")
    public ResponseEntity<List<ContenidoListDTO>> getAllContenidos() {
        List<ContenidoListDTO> contenidos = contenidoService.getAllContenidos();
        return ResponseEntity.ok(contenidos);
    }

    /**
     * GET /api/sub_menu/{id}/contenidos
     * Obtiene todos los contenidos de un SubMenu
     */
    @GetMapping("/sub_menu/{id}/contenidos")
    public ResponseEntity<List<ContenidoListDTO>> getContenidosBySubMenu(@PathVariable("id") Long subMenuId) {
        List<ContenidoListDTO> contenidos = contenidoService.getContenidosBySubMenu(subMenuId);
        return ResponseEntity.ok(contenidos);
    }

    /**
     * GET /api/contenido/{id}
     * Obtiene un contenido específico con todos sus bloques
     */
    @GetMapping("/contenido/{id}")
    public ResponseEntity<ContenidoEntity> getContenidoById(@PathVariable("id") Long id) {
        ContenidoEntity contenido = contenidoService.getContenidoById(id);
        return ResponseEntity.ok(contenido);
    }

    /**
     * POST /api/sub_menu/{id}/contenido
     * Crea un nuevo contenido vacío
     */
    @PostMapping("/sub_menu/{id}/contenido")
    public ResponseEntity<ContenidoEntity> createContenido(
            @PathVariable("id") Long subMenuId,
            @RequestBody ContenidoCreateRequestDTO request) {
        // Extraer usuario
        UsuarioEntity usuario = null;
        if (request.getUsuario_id() != null && request.getUsuario_id().get("id") != null) {
            Long userId = Long.parseLong(request.getUsuario_id().get("id").toString());
            usuario = usuarioRepository.findById(userId).orElse(null);
        }

        ContenidoEntity contenido = contenidoService.createContenido(
                subMenuId,
                request.getTitulo(),
                usuario);
        return ResponseEntity.ok(contenido);
    }

    /**
     * PUT /api/contenido/{id}
     * Actualiza el título, estado o contenido HTML de un contenido
     */
    @PutMapping("/contenido/{id}")
    public ResponseEntity<ContenidoEntity> updateContenido(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> updates) {
        String titulo = (String) updates.get("titulo");
        Boolean estado = (Boolean) updates.get("estado");
        String contenidoHtml = (String) updates.get("contenidoHtml");

        ContenidoEntity contenido = contenidoService.updateContenido(id, titulo, estado, contenidoHtml);
        return ResponseEntity.ok(contenido);
    }

    /**
     * DELETE /api/contenido/{id}
     * Elimina un contenido y todos sus bloques
     */
    @DeleteMapping("/contenido/{id}")
    public ResponseEntity<Void> deleteContenido(@PathVariable("id") Long id) {
        contenidoService.deleteContenido(id);
        return ResponseEntity.ok().build();
    }
}
