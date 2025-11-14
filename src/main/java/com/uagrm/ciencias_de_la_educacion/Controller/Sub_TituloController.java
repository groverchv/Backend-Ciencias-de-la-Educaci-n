package com.uagrm.ciencias_de_la_educacion.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uagrm.ciencias_de_la_educacion.Model.Sub_TituloEntity;
import com.uagrm.ciencias_de_la_educacion.Service.Sub_TituloService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("api/sub_titulo")
public class Sub_TituloController {
    @Autowired
    private Sub_TituloService sub_TituloService;

    @GetMapping("")
    public List<Sub_TituloEntity> getSubTitulos() {
        return sub_TituloService.getAllSubTitulos();
    }

    @GetMapping("/{id}")
    public Sub_TituloEntity getSubTituloById(@RequestParam Long id) {
        return sub_TituloService.getSubTituloById(id);
    }

    @PostMapping("")
    public Sub_TituloEntity createSubTitulo(@RequestBody Sub_TituloEntity sub_Titulo) {
        return sub_TituloService.saveSubTitulo(sub_Titulo);
    }

    @PutMapping("/{id}")
    public Sub_TituloEntity updateSubTitulo(@PathVariable Long id, @RequestBody Sub_TituloEntity sub_TituloDetails) {
        Sub_TituloEntity sub_Titulo = sub_TituloService.getSubTituloById(id);
        sub_Titulo.setNombre(sub_TituloDetails.getNombre());
        sub_Titulo.setEstado(sub_TituloDetails.getEstado());

        Sub_TituloEntity updatedSubTitulo = sub_TituloService.saveSubTitulo(sub_Titulo);
        return updatedSubTitulo;
    }

    public void deleteSubTitulo(Long id) {
        sub_TituloService.deleteSubTitulo(id);
    }
    
    


}
