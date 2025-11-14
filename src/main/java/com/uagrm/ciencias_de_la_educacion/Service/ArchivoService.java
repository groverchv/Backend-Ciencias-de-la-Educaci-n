package com.uagrm.ciencias_de_la_educacion.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uagrm.ciencias_de_la_educacion.Model.ArchivoEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.ArchivoRepository;

@Service
public class ArchivoService {
    @Autowired
    private ArchivoRepository archivoRepository;

    public List<ArchivoEntity> getAllArchivos() {
        return archivoRepository.findAll();
    }

    public ArchivoEntity saveArchivo(ArchivoEntity archivo) {
        return archivoRepository.save(archivo);
    }

    public ArchivoEntity getArchivoById(Long id) {
        return archivoRepository.findById(id).orElse(null);
    }

    public void deleteArchivo(Long id) {
        archivoRepository.deleteById(id);
    }

}
