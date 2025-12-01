package com.uagrm.ciencias_de_la_educacion.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uagrm.ciencias_de_la_educacion.Model.UsuarioEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UsuarioEntity> getUsuarios() {
        return usuarioRepository.findAll();
    }

    public UsuarioEntity guardarUsuario(UsuarioEntity usuario) {
        // Encriptar la contraseña antes de guardar si es una nueva contraseña
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            // Solo encriptar si no está ya encriptada (BCrypt empieza con $2a$, $2b$, o
            // $2y$)
            if (!usuario.getPassword().startsWith("$2")) {
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }
        }
        return usuarioRepository.save(usuario);
    }

    public UsuarioEntity buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public UsuarioEntity actualizarUsuario(Long id, UsuarioEntity usuarioDetalles) {
        UsuarioEntity usuarioExistente = usuarioRepository.findById(id).orElse(null);
        if (usuarioExistente != null) {
            usuarioExistente.setNombre(usuarioDetalles.getNombre());
            usuarioExistente.setApellido(usuarioDetalles.getApellido());
            usuarioExistente.setCorreo(usuarioDetalles.getCorreo());

            // Solo actualizar la contraseña si se proporciona una nueva
            if (usuarioDetalles.getPassword() != null && !usuarioDetalles.getPassword().isEmpty()) {
                // Solo encriptar si no está ya encriptada
                if (!usuarioDetalles.getPassword().startsWith("$2")) {
                    usuarioExistente.setPassword(passwordEncoder.encode(usuarioDetalles.getPassword()));
                }
            }

            usuarioExistente.setEstado(usuarioDetalles.getEstado());

            return usuarioRepository.save(usuarioExistente);
        }
        return null;
    }
}
