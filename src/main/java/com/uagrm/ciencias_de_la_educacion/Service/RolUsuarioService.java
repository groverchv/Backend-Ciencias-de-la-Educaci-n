package com.uagrm.ciencias_de_la_educacion.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uagrm.ciencias_de_la_educacion.Model.RolEntity;
import com.uagrm.ciencias_de_la_educacion.Model.RolUsuarioEntity;
import com.uagrm.ciencias_de_la_educacion.Model.UsuarioEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.RolRepository;
import com.uagrm.ciencias_de_la_educacion.Repository.RolUsuarioRepository;

@Service
public class RolUsuarioService {

    @Autowired
    private RolUsuarioRepository rolUsuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    /**
     * Obtiene todos los roles de un usuario
     */
    public List<RolEntity> getRolesByUsuarioId(Long usuarioId) {
        return rolUsuarioRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(RolUsuarioEntity::getRol)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los nombres de los roles de un usuario
     */
    public List<String> getRoleNamesByUsuarioId(Long usuarioId) {
        return rolUsuarioRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(ru -> ru.getRol().getNombre())
                .collect(Collectors.toList());
    }

    /**
     * Asigna un rol a un usuario
     */
    public RolUsuarioEntity assignRolToUsuario(UsuarioEntity usuario, RolEntity rol) {
        RolUsuarioEntity rolUsuario = new RolUsuarioEntity();
        rolUsuario.setUsuario(usuario);
        rolUsuario.setRol(rol);
        return rolUsuarioRepository.save(rolUsuario);
    }

    /**
     * Asigna un rol a un usuario por ID
     */
    public RolUsuarioEntity assignRolToUsuario(UsuarioEntity usuario, Long rolId) {
        RolEntity rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + rolId));
        return assignRolToUsuario(usuario, rol);
    }

    /**
     * Asigna un rol a un usuario por nombre de rol
     */
    public RolUsuarioEntity assignRolToUsuario(UsuarioEntity usuario, String rolNombre) {
        RolEntity rol = rolRepository.findByNombre(rolNombre)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con nombre: " + rolNombre));
        return assignRolToUsuario(usuario, rol);
    }

    /**
     * Elimina todos los roles de un usuario
     */
    @Transactional
    public void deleteRolesByUsuarioId(Long usuarioId) {
        rolUsuarioRepository.deleteByUsuarioId(usuarioId);
    }

    /**
     * Elimina todas las asignaciones de roles de un usuario
     */
    public List<RolUsuarioEntity> getRolUsuarioByUsuarioId(Long usuarioId) {
        return rolUsuarioRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Obtiene todas las asignaciones de roles a usuarios
     */
    public List<RolUsuarioEntity> getAllRolUsuarios() {
        return rolUsuarioRepository.findAll();
    }

    /**
     * Obtiene una asignación específica por ID
     */
    public RolUsuarioEntity getRolUsuarioById(Long id) {
        return rolUsuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignación de rol no encontrada con ID: " + id));
    }

    /**
     * Crea una nueva asignación de rol a usuario
     */
    public RolUsuarioEntity createRolUsuario(RolUsuarioEntity rolUsuario) {
        return rolUsuarioRepository.save(rolUsuario);
    }

    /**
     * Actualiza una asignación existente
     */
    public RolUsuarioEntity updateRolUsuario(Long id, RolUsuarioEntity rolUsuarioDetails) {
        RolUsuarioEntity rolUsuario = getRolUsuarioById(id);
        rolUsuario.setRol(rolUsuarioDetails.getRol());
        rolUsuario.setUsuario(rolUsuarioDetails.getUsuario());
        return rolUsuarioRepository.save(rolUsuario);
    }

    /**
     * Elimina una asignación de rol
     */
    public void deleteRolUsuario(Long id) {
        rolUsuarioRepository.deleteById(id);
    }
}
