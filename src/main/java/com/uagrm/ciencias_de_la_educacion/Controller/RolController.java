package com.uagrm.ciencias_de_la_educacion.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.uagrm.ciencias_de_la_educacion.Model.RolEntity;
import com.uagrm.ciencias_de_la_educacion.Service.RolService;

@RestController
@RequestMapping("/api/rol")
public class RolController {
    
    @Autowired
    private RolService rolService;

    @GetMapping("")
    public List<RolEntity> getAllRoles() {
        return rolService.getAllRoles();
    }

    @GetMapping("/{id}")
    public RolEntity getRolById(@PathVariable Long id) {
        return rolService.getRolById(id);
    }

    @PostMapping("")
    public RolEntity createRol(@RequestBody RolEntity rol) {
        return rolService.saveRol(rol);
    }

    @PutMapping("/{id}")
    public RolEntity updateRol(@PathVariable Long id, @RequestBody RolEntity rolDetails) {
        RolEntity rol = rolService.getRolById(id);
        if (rol != null) {
            rol.setNombre(rolDetails.getNombre());
            rol.setEstado(rolDetails.getEstado());
            return rolService.saveRol(rol);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteRol(@PathVariable Long id) {
        rolService.deleteRol(id);
    }
}
