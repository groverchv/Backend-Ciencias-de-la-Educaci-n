package com.uagrm.ciencias_de_la_educacion.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uagrm.ciencias_de_la_educacion.Model.PermisoEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.PermisoRepository;

@Service
public class PermisoService {
    
    @Autowired
    private PermisoRepository permisoRepository;

    public List<PermisoEntity> getAllPermisos() {
        return permisoRepository.findAll();
    }

    public PermisoEntity getPermisoById(Long id) {
        return permisoRepository.findById(id).orElse(null);
    }

    public PermisoEntity savePermiso(PermisoEntity permiso) {
        return permisoRepository.save(permiso);
    }

    public void deletePermiso(Long id) {
        permisoRepository.deleteById(id);
    }
}
