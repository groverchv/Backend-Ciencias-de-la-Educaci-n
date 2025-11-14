package com.uagrm.ciencias_de_la_educacion.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uagrm.ciencias_de_la_educacion.Model.PresentacionEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.PresentacionRepository;

@Service
public class PresentacionService {
    @Autowired
    private PresentacionRepository presentacionRepository;

    public List<PresentacionEntity> getPresentaciones() {
        return presentacionRepository.findAll();
    }

    public PresentacionEntity guardarPresentacion(PresentacionEntity presentacion) {
        return presentacionRepository.save(presentacion);
    }

    public PresentacionEntity buscarPorId(Long id) {
        return presentacionRepository.findById(id).orElse(null);
    }

    public void eliminarPresentacion(Long id) {
        presentacionRepository.deleteById(id);
    }

}
