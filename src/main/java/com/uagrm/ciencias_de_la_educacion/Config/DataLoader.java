package com.uagrm.ciencias_de_la_educacion.Config;

import com.uagrm.ciencias_de_la_educacion.Model.*;
import com.uagrm.ciencias_de_la_educacion.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private RolPermisoRepository rolPermisoRepository;

    @Autowired
    private RolUsuarioRepository rolUsuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("\n========================================");
        System.out.println("🚀 INICIANDO CARGA DE DATOS DEL SISTEMA");
        System.out.println("========================================\n");
        
        cargarPermisos();
        cargarRoles();
        asignarPermisosARoles();
        cargarUsuarios();
        
        System.out.println("\n========================================");
        System.out.println("✅ CARGA DE DATOS COMPLETADA");
        System.out.println("========================================\n");
    }

    private void cargarPermisos() {
        System.out.println("📋 Cargando permisos del sistema...");
        
        String[] permisos = {
            // Permisos de Usuarios
            "USUARIO_LISTAR",
            "USUARIO_VER",
            "USUARIO_CREAR",
            "USUARIO_EDITAR",
            "USUARIO_ELIMINAR",
            "USUARIO_CAMBIAR_ESTADO",
            
            // Permisos de Roles
            "ROL_LISTAR",
            "ROL_VER",
            "ROL_CREAR",
            "ROL_EDITAR",
            "ROL_ELIMINAR",
            "ROL_ASIGNAR_PERMISOS",
            
            // Permisos de Menús
            "MENU_LISTAR",
            "MENU_VER",
            "MENU_CREAR",
            "MENU_EDITAR",
            "MENU_ELIMINAR",
            
            // Permisos de Sub-Menús
            "SUBMENU_LISTAR",
            "SUBMENU_VER",
            "SUBMENU_CREAR",
            "SUBMENU_EDITAR",
            "SUBMENU_ELIMINAR",
            
            // Permisos de Contenido/Bloques
            "CONTENIDO_LISTAR",
            "CONTENIDO_VER",
            "CONTENIDO_CREAR",
            "CONTENIDO_EDITAR",
            "CONTENIDO_ELIMINAR",
            "CONTENIDO_PUBLICAR",
            
            // Permisos de Presentaciones
            "PRESENTACION_LISTAR",
            "PRESENTACION_VER",
            "PRESENTACION_CREAR",
            "PRESENTACION_EDITAR",
            "PRESENTACION_ELIMINAR",
            
            // Permisos de Bitácora
            "BITACORA_LISTAR",
            "BITACORA_VER",
            "BITACORA_ELIMINAR",
            "BITACORA_EXPORTAR",
            
            // Permisos Administrativos
            "ADMIN_PANEL_ACCEDER",
            "ADMIN_CONFIGURACION",
            "ADMIN_REPORTES",
            "ADMIN_AUDITORIA",
            
            // Permisos Especiales
            "SISTEMA_CONFIGURAR",
            "SISTEMA_BACKUP",
            "SISTEMA_RESTAURAR"
        };

        int creados = 0;
        for (String nombrePermiso : permisos) {
            if (!permisoRepository.existsByNombre(nombrePermiso)) {
                PermisoEntity permiso = new PermisoEntity();
                permiso.setNombre(nombrePermiso);
                permiso.setEstado(true);
                permisoRepository.save(permiso);
                creados++;
            }
        }
        
        System.out.println("   ✓ " + creados + " permisos creados");
        System.out.println("   ✓ Total de permisos en sistema: " + permisoRepository.count());
    }

    private void cargarRoles() {
        System.out.println("\n👥 Cargando roles del sistema...");
        
        // Rol Administrador
        if (!rolRepository.existsByNombre("ADMINISTRADOR")) {
            RolEntity adminRole = new RolEntity();
            adminRole.setNombre("ADMINISTRADOR");
            adminRole.setEstado(true);
            rolRepository.save(adminRole);
            System.out.println("   ✓ Rol 'ADMINISTRADOR' creado");
        }
        
        // Rol Usuario Estándar
        if (!rolRepository.existsByNombre("USUARIO")) {
            RolEntity userRole = new RolEntity();
            userRole.setNombre("USUARIO");
            userRole.setEstado(true);
            rolRepository.save(userRole);
            System.out.println("   ✓ Rol 'USUARIO' creado");
        }
        
        // Rol Editor de Contenido
        if (!rolRepository.existsByNombre("EDITOR")) {
            RolEntity editorRole = new RolEntity();
            editorRole.setNombre("EDITOR");
            editorRole.setEstado(true);
            rolRepository.save(editorRole);
            System.out.println("   ✓ Rol 'EDITOR' creado");
        }
        
        System.out.println("   ✓ Total de roles en sistema: " + rolRepository.count());
    }

    private void asignarPermisosARoles() {
        System.out.println("\n🔐 Asignando permisos a roles...");
        
        RolEntity adminRole = rolRepository.findByNombre("ADMINISTRADOR").orElse(null);
        RolEntity userRole = rolRepository.findByNombre("USUARIO").orElse(null);
        RolEntity editorRole = rolRepository.findByNombre("EDITOR").orElse(null);
        
        if (adminRole != null) {
            // El administrador tiene TODOS los permisos
            List<PermisoEntity> todosLosPermisos = permisoRepository.findAll();
            int asignados = 0;
            
            for (PermisoEntity permiso : todosLosPermisos) {
                // Verificar si ya existe la relación
                List<RolPermisoEntity> existente = rolPermisoRepository.findByRol_Id(adminRole.getId());
                boolean yaAsignado = existente.stream()
                    .anyMatch(rp -> rp.getPermiso().getId().equals(permiso.getId()));
                
                if (!yaAsignado) {
                    RolPermisoEntity rolPermiso = new RolPermisoEntity();
                    rolPermiso.setRol(adminRole);
                    rolPermiso.setPermiso(permiso);
                    rolPermisoRepository.save(rolPermiso);
                    asignados++;
                }
            }
            System.out.println("   ✓ " + asignados + " permisos asignados a ADMINISTRADOR");
        }
        
        if (editorRole != null) {
            // El editor tiene permisos de contenido, menús y sub-menús
            String[] permisosEditor = {
                "MENU_LISTAR", "MENU_VER", "MENU_CREAR", "MENU_EDITAR",
                "SUBMENU_LISTAR", "SUBMENU_VER", "SUBMENU_CREAR", "SUBMENU_EDITAR",
                "CONTENIDO_LISTAR", "CONTENIDO_VER", "CONTENIDO_CREAR", 
                "CONTENIDO_EDITAR", "CONTENIDO_PUBLICAR",
                "PRESENTACION_LISTAR", "PRESENTACION_VER", "PRESENTACION_CREAR", "PRESENTACION_EDITAR"
            };
            
            int asignados = 0;
            for (String nombrePermiso : permisosEditor) {
                PermisoEntity permiso = permisoRepository.findByNombre(nombrePermiso).orElse(null);
                if (permiso != null) {
                    List<RolPermisoEntity> existente = rolPermisoRepository.findByRol_Id(editorRole.getId());
                    boolean yaAsignado = existente.stream()
                        .anyMatch(rp -> rp.getPermiso().getId().equals(permiso.getId()));
                    
                    if (!yaAsignado) {
                        RolPermisoEntity rolPermiso = new RolPermisoEntity();
                        rolPermiso.setRol(editorRole);
                        rolPermiso.setPermiso(permiso);
                        rolPermisoRepository.save(rolPermiso);
                        asignados++;
                    }
                }
            }
            System.out.println("   ✓ " + asignados + " permisos asignados a EDITOR");
        }
        
        if (userRole != null) {
            // El usuario estándar tiene permisos básicos de lectura
            String[] permisosUsuario = {
                "MENU_LISTAR", "MENU_VER",
                "SUBMENU_LISTAR", "SUBMENU_VER",
                "CONTENIDO_LISTAR", "CONTENIDO_VER",
                "PRESENTACION_LISTAR", "PRESENTACION_VER"
            };
            
            int asignados = 0;
            for (String nombrePermiso : permisosUsuario) {
                PermisoEntity permiso = permisoRepository.findByNombre(nombrePermiso).orElse(null);
                if (permiso != null) {
                    List<RolPermisoEntity> existente = rolPermisoRepository.findByRol_Id(userRole.getId());
                    boolean yaAsignado = existente.stream()
                        .anyMatch(rp -> rp.getPermiso().getId().equals(permiso.getId()));
                    
                    if (!yaAsignado) {
                        RolPermisoEntity rolPermiso = new RolPermisoEntity();
                        rolPermiso.setRol(userRole);
                        rolPermiso.setPermiso(permiso);
                        rolPermisoRepository.save(rolPermiso);
                        asignados++;
                    }
                }
            }
            System.out.println("   ✓ " + asignados + " permisos asignados a USUARIO");
        }
    }

    private void cargarUsuarios() {
        System.out.println("\n👤 Cargando usuarios del sistema...");
        
        RolEntity adminRole = rolRepository.findByNombre("ADMINISTRADOR").orElse(null);
        RolEntity userRole = rolRepository.findByNombre("USUARIO").orElse(null);
        
        // Crear usuario administrador
        if (!usuarioRepository.existsByCorreo("admin@ciencias.uagrm.edu.bo")) {
            UsuarioEntity admin = new UsuarioEntity();
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setCorreo("admin@ciencias.uagrm.edu.bo");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEstado(true);

            Set<String> roles = new HashSet<>();
            roles.add("ADMIN");
            admin.setRoles(roles);

            admin = usuarioRepository.save(admin);
            
            // Asignar rol de Administrador en la tabla Rol_Usuario
            if (adminRole != null) {
                RolUsuarioEntity rolUsuario = new RolUsuarioEntity();
                rolUsuario.setUsuario(admin);
                rolUsuario.setRol(adminRole);
                rolUsuarioRepository.save(rolUsuario);
            }
            
            System.out.println("   ✓ Usuario administrador creado");
            System.out.println("      📧 Correo: admin@ciencias.uagrm.edu.bo");
            System.out.println("      🔑 Contraseña: admin123");
        }

        // Crear usuario de prueba
        if (!usuarioRepository.existsByCorreo("usuario@ciencias.uagrm.edu.bo")) {
            UsuarioEntity usuario = new UsuarioEntity();
            usuario.setNombre("Usuario");
            usuario.setApellido("Prueba");
            usuario.setCorreo("usuario@ciencias.uagrm.edu.bo");
            usuario.setPassword(passwordEncoder.encode("usuario123"));
            usuario.setEstado(true);

            Set<String> roles = new HashSet<>();
            roles.add("USER");
            usuario.setRoles(roles);

            usuario = usuarioRepository.save(usuario);
            
            // Asignar rol de Usuario en la tabla Rol_Usuario
            if (userRole != null) {
                RolUsuarioEntity rolUsuario = new RolUsuarioEntity();
                rolUsuario.setUsuario(usuario);
                rolUsuario.setRol(userRole);
                rolUsuarioRepository.save(rolUsuario);
            }
            
            System.out.println("   ✓ Usuario de prueba creado");
            System.out.println("      📧 Correo: usuario@ciencias.uagrm.edu.bo");
            System.out.println("      🔑 Contraseña: usuario123");
        }
    }
}
