package com.uagrm.ciencias_de_la_educacion.Config;

import com.uagrm.ciencias_de_la_educacion.Model.UsuarioEntity;

import com.uagrm.ciencias_de_la_educacion.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        cargarUsuarios();

    }

    private void cargarUsuarios() {
        // Verificar si ya existe el usuario admin
        if (!usuarioRepository.existsByCorreo("admin@ciencias.uagrm.edu.bo")) {
            // Crear usuario administrador por defecto
            UsuarioEntity admin = new UsuarioEntity();
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setCorreo("admin@ciencias.uagrm.edu.bo");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEstado(true);

            Set<String> roles = new HashSet<>();
            roles.add("ADMIN");
            roles.add("USER");
            admin.setRoles(roles);

            usuarioRepository.save(admin);
            System.out.println("✅ Usuario administrador creado exitosamente");
            System.out.println("   Correo: admin@ciencias.uagrm.edu.bo");
            System.out.println("   Contraseña: admin123");
        }

        // Crear usuario de prueba si no existe
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

            usuarioRepository.save(usuario);
            System.out.println("✅ Usuario de prueba creado exitosamente");
            System.out.println("   Correo: usuario@ciencias.uagrm.edu.bo");
            System.out.println("   Contraseña: usuario123");
        }
    }

}
