package com.uagrm.ciencias_de_la_educacion.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uagrm.ciencias_de_la_educacion.Model.UsuarioEntity;
import com.uagrm.ciencias_de_la_educacion.Service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("")
    public ResponseEntity<List<UsuarioEntity>> listarUsuarios() {
        List<UsuarioEntity> usuarios = usuarioService.getUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioEntity> obtenerUsuario(@PathVariable Long id) {
        UsuarioEntity usuario = usuarioService.buscarPorId(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("")
    public ResponseEntity<UsuarioEntity> crearUsuario(@RequestBody UsuarioEntity usuario) {
        UsuarioEntity nuevoUsuario = usuarioService.guardarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioEntity> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioEntity usuarioDetalles) {
        UsuarioEntity usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioDetalles);
        if (usuarioActualizado != null) {
            return ResponseEntity.ok(usuarioActualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        UsuarioEntity usuario = usuarioService.buscarPorId(id);
        if (usuario != null) {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioEntity> toggleEstado(@PathVariable Long id) {
        UsuarioEntity usuario = usuarioService.buscarPorId(id);
        if (usuario != null) {
            usuario.setEstado(!usuario.getEstado());
            UsuarioEntity usuarioActualizado = usuarioService.guardarUsuario(usuario);
            return ResponseEntity.ok(usuarioActualizado);
        }
        return ResponseEntity.notFound().build();
    }
}
