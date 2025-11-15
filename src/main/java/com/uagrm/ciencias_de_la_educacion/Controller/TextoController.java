package com.uagrm.ciencias_de_la_educacion.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uagrm.ciencias_de_la_educacion.Model.TextoEntity;
import com.uagrm.ciencias_de_la_educacion.Service.TextoService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/texto")
public class TextoController {
    @Autowired
    private TextoService textoService;

    @GetMapping("")
    public List<TextoEntity> ListarTextos() {
        return textoService.getAllTextos();
    }

    @GetMapping("/{id}")
    public TextoEntity getTextoById(@PathVariable Long id) {
        return textoService.getTextoById(id);
    }

    @PostMapping("")
    public TextoEntity createTexto(@RequestBody TextoEntity texto) {
        return textoService.saveTexto(null);
    }

    @PutMapping("/{id}")
    public TextoEntity updateTexto(@PathVariable Long id, @RequestBody TextoEntity textoDetails) {
        return textoService.saveTexto(textoDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteTexto(@PathVariable Long id) {
        textoService.deleteTexto(id);
    }

}
