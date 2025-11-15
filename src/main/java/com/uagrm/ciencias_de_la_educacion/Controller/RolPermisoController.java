package com.uagrm.ciencias_de_la_educacion.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uagrm.ciencias_de_la_educacion.Model.PermisoEntity;
import com.uagrm.ciencias_de_la_educacion.Service.RolPermisoService;

@RestController
@RequestMapping("/api/rol-permiso")
public class RolPermisoController {
    
    @Autowired
    private RolPermisoService rolPermisoService;

    /**
     * GET /api/rol-permiso/{rolId}
     * Obtiene todos los permisos asignados a un rol
     */
    @GetMapping("/{rolId}")
    public ResponseEntity<List<PermisoEntity>> getPermisosByRol(@PathVariable Long rolId) {
        try {
            List<PermisoEntity> permisos = rolPermisoService.getPermisosByRol(rolId);
            return ResponseEntity.ok(permisos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * POST /api/rol-permiso/{rolId}
     * Asigna múltiples permisos a un rol (reemplaza los existentes)
     * Body: { "permisosIds": [1, 2, 3, ...] }
     */
    @PostMapping("/{rolId}")
    public ResponseEntity<?> asignarPermisosARol(
            @PathVariable Long rolId,
            @RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> permisosIds = request.get("permisosIds");
            rolPermisoService.asignarPermisosARol(rolId, permisosIds);
            return ResponseEntity.ok().body(Map.of("message", "Permisos asignados correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/rol-permiso/{rolId}/agregar/{permisoId}
     * Agrega un permiso individual a un rol
     */
    @PutMapping("/{rolId}/agregar/{permisoId}")
    public ResponseEntity<?> agregarPermisoARol(
            @PathVariable Long rolId,
            @PathVariable Long permisoId) {
        try {
            rolPermisoService.agregarPermisoARol(rolId, permisoId);
            return ResponseEntity.ok().body(Map.of("message", "Permiso agregado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/rol-permiso/{rolId}/quitar/{permisoId}
     * Elimina un permiso de un rol
     */
    @DeleteMapping("/{rolId}/quitar/{permisoId}")
    public ResponseEntity<?> quitarPermisoDeRol(
            @PathVariable Long rolId,
            @PathVariable Long permisoId) {
        try {
            rolPermisoService.quitarPermisoDeRol(rolId, permisoId);
            return ResponseEntity.ok().body(Map.of("message", "Permiso eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
