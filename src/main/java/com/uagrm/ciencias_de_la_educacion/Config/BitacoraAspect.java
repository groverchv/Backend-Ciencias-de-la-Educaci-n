package com.uagrm.ciencias_de_la_educacion.Config;

import com.uagrm.ciencias_de_la_educacion.Model.BitacoraEntity;
import com.uagrm.ciencias_de_la_educacion.Model.UsuarioEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.BitacoraRepository;
import com.uagrm.ciencias_de_la_educacion.Security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class BitacoraAspect {

    @Autowired
    private BitacoraRepository bitacoraRepository;

    /**
     * Registra acciones POST (Crear)
     */
    @AfterReturning(
        pointcut = "execution(* com.uagrm.ciencias_de_la_educacion.Controller.*.create*(..)) || " +
                   "execution(* com.uagrm.ciencias_de_la_educacion.Controller.*.save*(..))",
        returning = "result"
    )
    public void logCreacion(JoinPoint joinPoint, Object result) {
        String nombreMetodo = joinPoint.getSignature().getName();
        String entidad = extraerNombreEntidad(joinPoint);
        
        registrarAccion(
            "CREAR",
            entidad,
            "Se creó un nuevo registro de " + entidad + " mediante " + nombreMetodo
        );
    }

    /**
     * Registra acciones PUT (Actualizar)
     */
    @AfterReturning(
        pointcut = "execution(* com.uagrm.ciencias_de_la_educacion.Controller.*.update*(..)) || " +
                   "execution(* com.uagrm.ciencias_de_la_educacion.Controller.*.actualizar*(..))",
        returning = "result"
    )
    public void logActualizacion(JoinPoint joinPoint, Object result) {
        String nombreMetodo = joinPoint.getSignature().getName();
        String entidad = extraerNombreEntidad(joinPoint);
        Object[] args = joinPoint.getArgs();
        Long id = extraerIdDeArgumentos(args);
        
        registrarAccion(
            "ACTUALIZAR",
            entidad,
            "Se actualizó el registro " + (id != null ? "ID: " + id : "") + " de " + entidad
        );
    }

    /**
     * Registra acciones DELETE (Eliminar)
     */
    @AfterReturning(
        pointcut = "execution(* com.uagrm.ciencias_de_la_educacion.Controller.*.delete*(..)) || " +
                   "execution(* com.uagrm.ciencias_de_la_educacion.Controller.*.eliminar*(..))",
        returning = "result"
    )
    public void logEliminacion(JoinPoint joinPoint, Object result) {
        String entidad = extraerNombreEntidad(joinPoint);
        Object[] args = joinPoint.getArgs();
        Long id = extraerIdDeArgumentos(args);
        
        registrarAccion(
            "ELIMINAR",
            entidad,
            "Se eliminó el registro " + (id != null ? "ID: " + id : "") + " de " + entidad
        );
    }

    /**
     * Registra acciones de LOGIN
     */
    @AfterReturning(
        pointcut = "execution(* com.uagrm.ciencias_de_la_educacion.Controller.AuthController.login(..))",
        returning = "result"
    )
    public void logLogin(JoinPoint joinPoint, Object result) {
        registrarAccion(
            "LOGIN",
            "Autenticacion",
            "Usuario inició sesión en el sistema"
        );
    }

    /**
     * Registra acciones de asignación de permisos a roles
     */
    @AfterReturning(
        pointcut = "execution(* com.uagrm.ciencias_de_la_educacion.Controller.RolPermisoController.asignarPermisosARol(..))",
        returning = "result"
    )
    public void logAsignacionPermisos(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long rolId = extraerIdDeArgumentos(args);
        
        registrarAccion(
            "ASIGNAR_PERMISOS",
            "Rol_Permiso",
            "Se asignaron permisos al rol ID: " + rolId
        );
    }

    /**
     * Registra guardado de bloques de contenido
     */
    @AfterReturning(
        pointcut = "execution(* com.uagrm.ciencias_de_la_educacion.Controller.BloqueController.saveBloques(..))",
        returning = "result"
    )
    public void logGuardarBloques(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long subMenuId = extraerIdDeArgumentos(args);
        
        registrarAccion(
            "ACTUALIZAR_CONTENIDO",
            "Bloque_Contenido",
            "Se actualizaron los bloques del SubMenu ID: " + subMenuId
        );
    }

    /**
     * Método auxiliar para registrar la acción en la base de datos
     */
    private void registrarAccion(String accion, String tablaAfectada, String descripcion) {
        try {
            BitacoraEntity bitacora = new BitacoraEntity();
            bitacora.setAccion(accion);
            bitacora.setTabla_afecta(tablaAfectada);
            bitacora.setDescripcion(descripcion);
            bitacora.setIp_origen(obtenerIpCliente());
            bitacora.setFecha(LocalDateTime.now());
            
            // Intentar obtener el usuario actual
            UsuarioEntity usuario = obtenerUsuarioActual();
            if (usuario != null) {
                bitacora.setUsuario_id(usuario);
            }
            
            bitacoraRepository.save(bitacora);
        } catch (Exception e) {
            // No lanzar excepción para no interrumpir el flujo normal
            System.err.println("⚠️ Error al registrar en bitácora: " + e.getMessage());
        }
    }

    /**
     * Extrae el nombre de la entidad desde el nombre del controlador
     */
    private String extraerNombreEntidad(JoinPoint joinPoint) {
        String nombreClase = joinPoint.getTarget().getClass().getSimpleName();
        // Ejemplo: "UsuarioController" -> "Usuario"
        return nombreClase.replace("Controller", "").replace("$", "");
    }

    /**
     * Extrae el ID del primer argumento Long encontrado
     */
    private Long extraerIdDeArgumentos(Object[] args) {
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg instanceof Long) {
                    return (Long) arg;
                }
            }
        }
        return null;
    }

    /**
     * Obtiene el usuario actualmente autenticado
     */
    private UsuarioEntity obtenerUsuarioActual() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                UsuarioEntity usuario = new UsuarioEntity();
                usuario.setId(userDetails.getId());
                return usuario;
            }
        } catch (Exception e) {
            // Usuario no autenticado o contexto no disponible
        }
        return null;
    }

    /**
     * Obtiene la IP del cliente desde la solicitud HTTP
     */
    private String obtenerIpCliente() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ipAddress = request.getHeader("X-Forwarded-For");
                
                if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                    ipAddress = request.getRemoteAddr();
                } else {
                    ipAddress = ipAddress.split(",")[0].trim();
                }
                
                return ipAddress;
            }
        } catch (Exception e) {
            // No hay contexto de request disponible
        }
        return "0.0.0.0";
    }
}
