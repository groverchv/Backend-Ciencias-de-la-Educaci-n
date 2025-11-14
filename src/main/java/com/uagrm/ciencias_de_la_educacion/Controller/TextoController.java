package com.uagrm.ciencias_de_la_educacion.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uagrm.ciencias_de_la_educacion.Service.TextoService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public TextoService getAllTextos() {
        return textoService;
    }

    @GetMapping("/{id}")
    public TextoService getTextoById(@RequestParam Long id) {
        return textoService;
    }

    @PostMapping("")
    public TextoService createTexto(@RequestBody TextoService texto) {
        return textoService;
    }

    @PutMapping("/{id}")
    public TextoService updateTexto(@PathVariable Long id, @RequestBody TextoService textoDetails) {
        return textoService;
    }

    @DeleteMapping("/{id}")
    public void deleteTexto(@PathVariable Long id) {
        textoService.deleteTexto(id);
    }
    
    
    
    
}
