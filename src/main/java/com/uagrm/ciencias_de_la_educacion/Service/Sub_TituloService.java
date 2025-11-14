package com.uagrm.ciencias_de_la_educacion.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uagrm.ciencias_de_la_educacion.Model.Sub_TituloEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.Sub_TituloRepository;

@Service
public class Sub_TituloService {
    @Autowired
    private Sub_TituloRepository sub_TituloRepository;

    public List<Sub_TituloEntity> getAllSubTitulos() {
        return sub_TituloRepository.findAll();
    }

    public Sub_TituloEntity saveSubTitulo(Sub_TituloEntity subTitulo) {
        return sub_TituloRepository.save(subTitulo);
    }

    public Sub_TituloEntity getSubTituloById(Long id) {
        return sub_TituloRepository.findById(id).orElse(null);
    }

    public void deleteSubTitulo(Long id) {
        sub_TituloRepository.deleteById(id);
    }
}
