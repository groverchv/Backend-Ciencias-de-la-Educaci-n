package com.uagrm.ciencias_de_la_educacion.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uagrm.ciencias_de_la_educacion.Model.PresentacionEntity;
import com.uagrm.ciencias_de_la_educacion.Service.PresentacionService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/presentacion")
public class PresentacionController {
    @Autowired
    private PresentacionService presentacionService;

    @GetMapping("")
    public List<PresentacionEntity> listarPresentaciones() {
        return presentacionService.getPresentaciones();
    }

    @RequestMapping("/{id}")
    public PresentacionEntity obtenerPresentacionPorId(Long id) {
        return presentacionService.buscarPorId(id);
    }

    @PostMapping("")
    public PresentacionEntity crearPresentacion(@RequestBody PresentacionEntity presentacion) {
        return presentacionService.guardarPresentacion(presentacion);
    }

    @PutMapping("/{id}")
    public PresentacionEntity actualizarPresentacion(@PathVariable Long id,
            @RequestBody PresentacionEntity presentacionDetalles) {
        PresentacionEntity presentacion = presentacionService.buscarPorId(id);
        presentacion.setUrl(presentacionDetalles.getUrl());
        presentacion.setEstado(presentacionDetalles.getEstado());

        PresentacionEntity updatedPresentacion = presentacionService.guardarPresentacion(presentacion);
        return updatedPresentacion;
    }

    @DeleteMapping("/{id}")
    public void eliminarPresentacion(@PathVariable Long id) {
        presentacionService.eliminarPresentacion(id);
    }

}
