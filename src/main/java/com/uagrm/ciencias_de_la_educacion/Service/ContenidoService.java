package com.uagrm.ciencias_de_la_educacion.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uagrm.ciencias_de_la_educacion.Model.ContenidoEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.ContenidoRepository;

@Service
public class ContenidoService {
    @Autowired
    private ContenidoRepository contenidoRepository;

    public List<ContenidoEntity> getAllContenidos() {
        return contenidoRepository.findAll();
    }

    public ContenidoEntity saveContenido(ContenidoEntity contenido) {
        return contenidoRepository.save(contenido);
    }

    public ContenidoEntity getContenidoById(Long id) {
        return contenidoRepository.findById(id).orElse(null);
    }

    public void deleteContenido(Long id) {
        contenidoRepository.deleteById(id);
    }
}
