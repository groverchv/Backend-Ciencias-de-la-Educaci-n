package com.uagrm.ciencias_de_la_educacion.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // ⛔ BLOQUEADO: Los permisos son estáticos y se cargan automáticamente
    @PostMapping("")
    public ResponseEntity<?> createPermiso(@RequestBody PermisoEntity permiso) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body("No se pueden crear permisos manualmente. Los permisos son estáticos y se cargan automáticamente al iniciar el sistema.");
    }

    // ⛔ BLOQUEADO: Los permisos son estáticos y no se pueden editar
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePermiso(@PathVariable Long id, @RequestBody PermisoEntity permisoDetails) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body("No se pueden editar permisos. Los permisos son estáticos y predefinidos por el sistema.");
    }

    // ⛔ BLOQUEADO: Los permisos son estáticos y no se pueden eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePermiso(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body("No se pueden eliminar permisos. Los permisos son estáticos y predefinidos por el sistema.");
    }
}
