package com.uagrm.ciencias_de_la_educacion.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uagrm.ciencias_de_la_educacion.Model.TituloEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.TituloRepository;

@Service
public class TituloService {
    @Autowired
    private TituloRepository tituloRepository;

    public List<TituloEntity> getAllTitulos() {
        return tituloRepository.findAll();
    }

    public TituloEntity saveTitulo(TituloEntity titulo) {
        return tituloRepository.save(titulo);
    }

    public TituloEntity getTituloById(Long id) {
        return tituloRepository.findById(id).orElse(null);
    }

    public void deleteTitulo(Long id) {
        tituloRepository.deleteById(id);
    }
}
