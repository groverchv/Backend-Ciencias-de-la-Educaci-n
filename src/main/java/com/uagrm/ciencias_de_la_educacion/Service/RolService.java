package com.uagrm.ciencias_de_la_educacion.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uagrm.ciencias_de_la_educacion.Model.RolEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.RolRepository;

@Service
public class RolService {
    
    @Autowired
    private RolRepository rolRepository;

    public List<RolEntity> getAllRoles() {
        return rolRepository.findAll();
    }

    public RolEntity getRolById(Long id) {
        return rolRepository.findById(id).orElse(null);
    }

    public RolEntity saveRol(RolEntity rol) {
        return rolRepository.save(rol);
    }

    public void deleteRol(Long id) {
        rolRepository.deleteById(id);
    }
}
