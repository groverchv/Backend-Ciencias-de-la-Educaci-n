package com.uagrm.ciencias_de_la_educacion.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uagrm.ciencias_de_la_educacion.Model.TextoEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.TextoRepository;

@Service
public class TextoService {
    @Autowired
    private TextoRepository textoRepository;

    public List<TextoEntity> getAllTextos() {
        return textoRepository.findAll();
    }

    public TextoEntity saveTexto(TextoEntity texto) {
        return textoRepository.save(texto);
    }

    public TextoEntity getTextoById(Long id) {
        return textoRepository.findById(id).orElse(null);
    }

    public void deleteTexto(Long id) {
        textoRepository.deleteById(id);
    }
}
