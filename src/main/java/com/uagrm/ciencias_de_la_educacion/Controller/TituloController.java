package com.uagrm.ciencias_de_la_educacion.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uagrm.ciencias_de_la_educacion.Model.TituloEntity;
import com.uagrm.ciencias_de_la_educacion.Service.TituloService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/api/titulo")
public class TituloController {
    @Autowired
    private TituloService tituloService;
    
    @GetMapping("")
    public List<TituloEntity> getTitulos() {
        return tituloService.getAllTitulos();
    }

    @GetMapping("/{id}")
    public TituloEntity getTituloById(@PathVariable Long id) {
        return tituloService.getTituloById(id);
    }

    @PostMapping("")
    public TituloEntity createTitulo(@RequestBody TituloEntity titulo) {
        return tituloService.saveTitulo(titulo);
    }

    @PutMapping("/{id}")
    public TituloEntity updateTitulo(@PathVariable Long id, @RequestBody TituloEntity tituloDetails) {
        TituloEntity titulo = tituloService.getTituloById(id);
        titulo.setNombre(tituloDetails.getNombre());
        titulo.setEstado(tituloDetails.getEstado());

        TituloEntity updatedTitulo = tituloService.saveTitulo(titulo);
        return updatedTitulo;
    }
    
    @DeleteMapping("/{id}")
    public void deleteTitulo(@PathVariable Long id) {
        tituloService.deleteTitulo(id);
    }
    


    
}
