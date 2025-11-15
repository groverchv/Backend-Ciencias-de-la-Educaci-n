package com.uagrm.ciencias_de_la_educacion.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.uagrm.ciencias_de_la_educacion.Model.PermisoEntity;
import com.uagrm.ciencias_de_la_educacion.Service.PermisoService;

@RestController
@RequestMapping("/api/permiso")
public class PermisoController {
    
    @Autowired
    private PermisoService permisoService;

    @GetMapping("")
    public List<PermisoEntity> getAllPermisos() {
        return permisoService.getAllPermisos();
    }

    @GetMapping("/{id}")
    public PermisoEntity getPermisoById(@PathVariable Long id) {
        return permisoService.getPermisoById(id);
    }

    @PostMapping("")
    public PermisoEntity createPermiso(@RequestBody PermisoEntity permiso) {
        return permisoService.savePermiso(permiso);
    }

    @PutMapping("/{id}")
    public PermisoEntity updatePermiso(@PathVariable Long id, @RequestBody PermisoEntity permisoDetails) {
        PermisoEntity permiso = permisoService.getPermisoById(id);
        if (permiso != null) {
            permiso.setNombre(permisoDetails.getNombre());
            permiso.setEstado(permisoDetails.getEstado());
            return permisoService.savePermiso(permiso);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deletePermiso(@PathVariable Long id) {
        permisoService.deletePermiso(id);
    }
}
