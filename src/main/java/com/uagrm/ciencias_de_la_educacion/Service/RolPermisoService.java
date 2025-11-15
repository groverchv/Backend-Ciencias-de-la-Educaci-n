package com.uagrm.ciencias_de_la_educacion.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uagrm.ciencias_de_la_educacion.Model.PermisoEntity;
import com.uagrm.ciencias_de_la_educacion.Model.RolEntity;
import com.uagrm.ciencias_de_la_educacion.Model.RolPermisoEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.PermisoRepository;
import com.uagrm.ciencias_de_la_educacion.Repository.RolPermisoRepository;
import com.uagrm.ciencias_de_la_educacion.Repository.RolRepository;

@Service
public class RolPermisoService {
    
    @Autowired
    private RolPermisoRepository rolPermisoRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private PermisoRepository permisoRepository;

    /**
     * Obtiene todos los permisos asignados a un rol
     */
    public List<PermisoEntity> getPermisosByRol(Long rolId) {
        List<RolPermisoEntity> rolPermisos = rolPermisoRepository.findByRol_Id(rolId);
        return rolPermisos.stream()
            .map(RolPermisoEntity::getPermiso)
            .collect(Collectors.toList());
    }

    /**
     * Asigna múltiples permisos a un rol (reemplaza los existentes)
     */
    @Transactional
    public void asignarPermisosARol(Long rolId, List<Long> permisosIds) {
        RolEntity rol = rolRepository.findById(rolId)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        
        // Eliminar permisos actuales del rol
        rolPermisoRepository.deleteByRol_Id(rolId);
        
        // Asignar nuevos permisos
        for (Long permisoId : permisosIds) {
            PermisoEntity permiso = permisoRepository.findById(permisoId)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado: " + permisoId));
            
            RolPermisoEntity rolPermiso = new RolPermisoEntity();
            rolPermiso.setRol(rol);
            rolPermiso.setPermiso(permiso);
            rolPermisoRepository.save(rolPermiso);
        }
    }

    /**
     * Agrega un permiso individual a un rol
     */
    public void agregarPermisoARol(Long rolId, Long permisoId) {
        RolEntity rol = rolRepository.findById(rolId)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        
        PermisoEntity permiso = permisoRepository.findById(permisoId)
            .orElseThrow(() -> new RuntimeException("Permiso no encontrado"));
        
        // Verificar si ya existe la asignación
        List<RolPermisoEntity> existente = rolPermisoRepository.findByRol_Id(rolId);
        boolean yaAsignado = existente.stream()
            .anyMatch(rp -> rp.getPermiso().getId().equals(permisoId));
        
        if (!yaAsignado) {
            RolPermisoEntity rolPermiso = new RolPermisoEntity();
            rolPermiso.setRol(rol);
            rolPermiso.setPermiso(permiso);
            rolPermisoRepository.save(rolPermiso);
        }
    }

    /**
     * Elimina un permiso de un rol
     */
    @Transactional
    public void quitarPermisoDeRol(Long rolId, Long permisoId) {
        List<RolPermisoEntity> rolPermisos = rolPermisoRepository.findByRol_Id(rolId);
        
        rolPermisos.stream()
            .filter(rp -> rp.getPermiso().getId().equals(permisoId))
            .findFirst()
            .ifPresent(rp -> rolPermisoRepository.delete(rp));
    }
}
