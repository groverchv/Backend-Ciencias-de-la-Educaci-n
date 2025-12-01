package com.uagrm.ciencias_de_la_educacion.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uagrm.ciencias_de_la_educacion.Model.RolUsuarioEntity;
import com.uagrm.ciencias_de_la_educacion.Service.RolUsuarioService;

@RestController
@RequestMapping("/api/rol_usuario")
public class RolUsuarioController {

    @Autowired
    private RolUsuarioService rolUsuarioService;

    /**
     * Obtener todas las asignaciones de roles a usuarios
     */
    @GetMapping("")
    public ResponseEntity<List<RolUsuarioEntity>> getAllRolUsuarios() {
        List<RolUsuarioEntity> rolUsuarios = rolUsuarioService.getAllRolUsuarios();
        return ResponseEntity.ok(rolUsuarios);
    }

    /**
     * Obtener una asignación específica por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<RolUsuarioEntity> getRolUsuarioById(@PathVariable Long id) {
        try {
            RolUsuarioEntity rolUsuario = rolUsuarioService.getRolUsuarioById(id);
            return ResponseEntity.ok(rolUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crear una nueva asignación (Asignar rol a usuario)
     */
    @PostMapping("")
    public ResponseEntity<RolUsuarioEntity> createRolUsuario(@RequestBody RolUsuarioEntity rolUsuario) {
        try {
            RolUsuarioEntity newRolUsuario = rolUsuarioService.createRolUsuario(rolUsuario);
            return ResponseEntity.ok(newRolUsuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualizar una asignación existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<RolUsuarioEntity> updateRolUsuario(
            @PathVariable Long id,
            @RequestBody RolUsuarioEntity rolUsuarioDetails) {
        try {
            RolUsuarioEntity updatedRolUsuario = rolUsuarioService.updateRolUsuario(id, rolUsuarioDetails);
            return ResponseEntity.ok(updatedRolUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar una asignación (Desasignar rol)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRolUsuario(@PathVariable Long id) {
        try {
            rolUsuarioService.deleteRolUsuario(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
