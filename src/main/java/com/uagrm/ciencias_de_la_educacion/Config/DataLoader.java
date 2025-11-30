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

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

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

        cargarRoles();
        cargarUsuarios();

        System.out.println("\n========================================");
        System.out.println("✅ CARGA DE DATOS COMPLETADA");
        System.out.println("========================================\n");
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
