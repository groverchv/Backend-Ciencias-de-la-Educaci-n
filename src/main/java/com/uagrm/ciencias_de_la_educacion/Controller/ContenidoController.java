package com.uagrm.ciencias_de_la_educacion.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uagrm.ciencias_de_la_educacion.Model.ContenidoEntity;
import com.uagrm.ciencias_de_la_educacion.Service.ContenidoService;

@RestController
@RequestMapping("api/contenido")
public class ContenidoController {
    @Autowired
    private ContenidoService contenidoService;

    public List<ContenidoEntity> listarContenidos() {
        return contenidoService.getAllContenidos();
    }

    public ContenidoEntity obtenerContenidoPorId(Long id) {
        return contenidoService.getContenidoById(id);
    }

    public ContenidoEntity crearContenido(ContenidoEntity contenido) {
        return contenidoService.saveContenido(contenido);
    }

    public ContenidoEntity actualizarContenido(Long id, ContenidoEntity contenidoDetalles) {
        ContenidoEntity contenido = contenidoService.getContenidoById(id);
        contenido.setOrden(contenidoDetalles.getOrden());
        contenido.setEstado(contenidoDetalles.getEstado());

        ContenidoEntity updatedContenido = contenidoService.saveContenido(contenido);
        return updatedContenido;
    }

    public void eliminarContenido(Long id) {
        contenidoService.deleteContenido(id);
    }
}
