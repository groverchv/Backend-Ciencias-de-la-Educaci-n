package com.uagrm.ciencias_de_la_educacion.Config;

import com.uagrm.ciencias_de_la_educacion.Model.*;
import com.uagrm.ciencias_de_la_educacion.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private Sub_MenuRepository subMenuRepository;

    @Autowired
    private ContenidoRepository contenidoRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("\n========================================");
        System.out.println("🚀 INICIANDO CARGA DE DATOS DEL SISTEMA");
        System.out.println("========================================\n");

        cargarRoles();
        cargarUsuarios();
        cargarContenidoInicial();

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

    private void cargarContenidoInicial() {
        System.out.println("\n📄 Cargando contenido inicial...");

        UsuarioEntity admin = usuarioRepository.findByCorreo("admin@ciencias.uagrm.edu.bo").orElse(null);
        if (admin == null)
            return;

        // 1. Crear Menu "Inicio"
        MenuEntity menuInicioTemp = menuRepository.findAll().stream()
                .filter(m -> "Inicio".equals(m.getTitulo()))
                .findFirst()
                .orElse(null);

        if (menuInicioTemp == null) {
            menuInicioTemp = new MenuEntity();
            menuInicioTemp.setTitulo("Inicio");
            menuInicioTemp.setRuta("/inicio");
            menuInicioTemp.setIcono("HomeOutlined");
            menuInicioTemp.setOrden(1);
            menuInicioTemp.setEstado(true);
            menuInicioTemp.setUsuario_id(admin);
            menuInicioTemp = menuRepository.save(menuInicioTemp);
            System.out.println("   ✓ Menú 'Inicio' creado");
        }

        final MenuEntity menuInicio = menuInicioTemp;

        // 2. Crear SubMenu "Bienvenido"
        Sub_MenuEntity subMenuBienvenidoTemp = subMenuRepository.findAll().stream()
                .filter(sm -> "Bienvenido".equals(sm.getTitulo()) && sm.getMenu_id().getId().equals(menuInicio.getId()))
                .findFirst()
                .orElse(null);

        if (subMenuBienvenidoTemp == null) {
            subMenuBienvenidoTemp = new Sub_MenuEntity();
            subMenuBienvenidoTemp.setTitulo("Bienvenido");
            subMenuBienvenidoTemp.setRuta("/inicio/bienvenido");
            subMenuBienvenidoTemp.setIcono("SmileOutlined");
            subMenuBienvenidoTemp.setOrden(1);
            subMenuBienvenidoTemp.setEstado(true);
            subMenuBienvenidoTemp.setMenu_id(menuInicio);
            subMenuBienvenidoTemp.setUsuario_id(admin);
            subMenuBienvenidoTemp = subMenuRepository.save(subMenuBienvenidoTemp);
            System.out.println("   ✓ SubMenú 'Bienvenido' creado");
        }

        final Sub_MenuEntity subMenuBienvenido = subMenuBienvenidoTemp;

        // 3. Crear Contenido
        if (contenidoRepository.countBySubMenu_Id(subMenuBienvenido.getId()) == 0) {
            String htmlContent = "<div style=\"padding: 40px 20px; max-width: 1200px; margin: 0 auto;\">" +
                    "<h1 style=\"text-align: center; margin-bottom: 40px; font-size: 38px; font-weight: 600;\">Bienvenidos a la Facultad de Humanidades</h1>"
                    +
                    "<p style=\"font-size: 18px; text-align: center; margin-bottom: 40px;\">Carrera de Ciencias de la Educación - UAGRM</p>"
                    +
                    "<div style=\"display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 24px; margin-bottom: 40px;\">"
                    +
                    "<div style=\"border: 1px solid #f0f0f0; border-radius: 8px; overflow: hidden; text-align: center; height: 100%; box-shadow: 0 1px 2px 0 rgba(0,0,0,0.03); transition: all 0.3s;\">"
                    +
                    "<div style=\"padding: 40px; font-size: 64px; color: #2600af;\">📖</div>" +
                    "<div style=\"padding: 24px;\"><h3 style=\"margin-bottom: 8px; font-weight: 600;\">Formación Académica</h3><p style=\"color: rgba(0,0,0,0.45);\">Programas de estudio y modalidades de graduación de excelencia</p></div></div>"
                    +
                    "<div style=\"border: 1px solid #f0f0f0; border-radius: 8px; overflow: hidden; text-align: center; height: 100%; box-shadow: 0 1px 2px 0 rgba(0,0,0,0.03); transition: all 0.3s;\">"
                    +
                    "<div style=\"padding: 40px; font-size: 64px; color: #2600af;\">👥</div>" +
                    "<div style=\"padding: 24px;\"><h3 style=\"margin-bottom: 8px; font-weight: 600;\">Comunidad Educativa</h3><p style=\"color: rgba(0,0,0,0.45);\">Docentes calificados y estudiantes comprometidos con la educación</p></div></div>"
                    +
                    "<div style=\"border: 1px solid #f0f0f0; border-radius: 8px; overflow: hidden; text-align: center; height: 100%; box-shadow: 0 1px 2px 0 rgba(0,0,0,0.03); transition: all 0.3s;\">"
                    +
                    "<div style=\"padding: 40px; font-size: 64px; color: #2600af;\">🧪</div>" +
                    "<div style=\"padding: 24px;\"><h3 style=\"margin-bottom: 8px; font-weight: 600;\">Investigación</h3><p style=\"color: rgba(0,0,0,0.45);\">Centro de investigación CISAD y proyectos académicos</p></div></div>"
                    +
                    "<div style=\"border: 1px solid #f0f0f0; border-radius: 8px; overflow: hidden; text-align: center; height: 100%; box-shadow: 0 1px 2px 0 rgba(0,0,0,0.03); transition: all 0.3s;\">"
                    +
                    "<div style=\"padding: 40px; font-size: 64px; color: #2600af;\">🌐</div>" +
                    "<div style=\"padding: 24px;\"><h3 style=\"margin-bottom: 8px; font-weight: 600;\">Convenios</h3><p style=\"color: rgba(0,0,0,0.45);\">Alianzas interfacultativas e interinstitucionales</p></div></div></div>"
                    +
                    "<div style=\"border: 1px solid #f0f0f0; border-radius: 8px; padding: 24px; margin-top: 40px; background: #fff;\">"
                    +
                    "<h3 style=\"margin-bottom: 16px; font-weight: 600; font-size: 24px;\">Sobre Nosotros</h3>" +
                    "<p style=\"font-size: 16px; margin-bottom: 16px; line-height: 1.6;\">La carrera de Ciencias de la Educación de la Universidad Autónoma Gabriel René Moreno forma profesionales comprometidos con la educación y el desarrollo social de nuestra región.</p>"
                    +
                    "<p style=\"font-size: 16px; margin-bottom: 0; line-height: 1.6;\">Contamos con un cuerpo docente altamente calificado, infraestructura moderna y programas académicos actualizados que garantizan una formación integral de nuestros estudiantes.</p></div></div>";

            ContenidoEntity contenido = new ContenidoEntity();
            contenido.setTitulo("Bienvenida");
            contenido.setSubMenu(subMenuBienvenido);
            contenido.setUsuario(admin);
            contenido.setOrden(1);
            contenido.setEstado(true);
            contenido.setContenidoHtml(htmlContent);
            contenido.setContenidoPublicado(htmlContent);

            contenidoRepository.save(contenido);
            System.out.println("   ✓ Contenido de bienvenida creado");
        }
    }
}
