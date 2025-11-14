package com.uagrm.ciencias_de_la_educacion.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uagrm.ciencias_de_la_educacion.Model.ArchivoEntity;
import com.uagrm.ciencias_de_la_educacion.Service.ArchivoService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("api/archivos")
public class ArchivoController {
    @Autowired
    private ArchivoService archivoService;

    @GetMapping("")
    public List<ArchivoEntity> listarArchivo() {
        return archivoService.getAllArchivos();
    }

    @GetMapping("/{id}")
    public ArchivoEntity obtenerArchivoPorId(@PathVariable Long id) {
        return archivoService.getArchivoById(id);
    }

    @PostMapping("")
    public ArchivoEntity guardarArchivo(@RequestBody ArchivoEntity archivo) {
        return archivoService.saveArchivo(archivo);
    }

    @PutMapping("/{id}")
    public ArchivoEntity actualizarArchivo(@PathVariable Long id, @RequestBody ArchivoEntity archivoDetalles) {
        ArchivoEntity archivoExistente = archivoService.getArchivoById(id);
        archivoExistente.setNombre(archivoDetalles.getNombre());
        archivoExistente.setDescripcion(archivoDetalles.getDescripcion());
        archivoExistente.setUrl(archivoDetalles.getUrl());
        archivoExistente.setEstado(archivoDetalles.getEstado());

        ArchivoEntity archivoActualizado = archivoService.saveArchivo(archivoExistente);
        return archivoActualizado;

    }

    @DeleteMapping("/{id}")
    public void eliminarArchivo(@PathVariable Long id) {
        archivoService.deleteArchivo(id);
    }

}
