package com.uagrm.ciencias_de_la_educacion.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.uagrm.ciencias_de_la_educacion.Model.ContenidoEntity;
import com.uagrm.ciencias_de_la_educacion.Service.ContenidoService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/contenido")
public class ContenidoController {

    @Autowired
    private ContenidoService contenidoService;

    @GetMapping("")
    public List<ContenidoEntity> listarContenidos() {
        return contenidoService.getAllContenidos();
    }

    @GetMapping("/{id}")
    public ContenidoEntity obtenerContenidoPorId(@PathVariable Long id) {
        return contenidoService.getContenidoById(id);
    }

    @PostMapping("")
    public ContenidoEntity crearContenido(@RequestBody ContenidoEntity contenido) {
        // Asumiendo que el frontend envía los objetos anidados completos (ej: { "id": 1 })
        return contenidoService.saveContenido(contenido);
    }

    @PutMapping("/{id}")
    public ContenidoEntity actualizarContenido(@PathVariable Long id, @RequestBody ContenidoEntity contenidoDetalles) {
        // 1. Obtener la entidad existente
        ContenidoEntity contenido = contenidoService.getContenidoById(id);
        
        // --- CAMBIO CLAVE: Actualizar TODOS los campos ---
        contenido.setOrden(contenidoDetalles.getOrden());
        contenido.setEstado(contenidoDetalles.getEstado());
        
        // Actualizar todas las relaciones
        contenido.setSub_menu_id(contenidoDetalles.getSub_menu_id());
        contenido.setTitulo_id(contenidoDetalles.getTitulo_id());
        contenido.setArchivo_id(contenidoDetalles.getArchivo_id());
        contenido.setSub_titulo_id(contenidoDetalles.getSub_titulo_id());
        contenido.setTexto_id(contenidoDetalles.getTexto_id());
        // --- FIN DEL CAMBIO ---

        // 2. Guardar la entidad actualizada
        ContenidoEntity updatedContenido = contenidoService.saveContenido(contenido);
        return updatedContenido;
    }

    @DeleteMapping("/{id}")
    public void eliminarContenido(@PathVariable Long id) {
        contenidoService.deleteContenido(id);
    }
}