package com.uagrm.ciencias_de_la_educacion.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    // Mapa de todos los usuarios conectados (sessionId -> username)
    private final Map<String, String> connectedUsers = new ConcurrentHashMap<>();
    // Mapa de ubicación de usuarios (sessionId -> location: "dashboard" o "other")
    private final Map<String, String> userLocations = new ConcurrentHashMap<>();

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Registrar un usuario conectado con su ubicación
     */
    public void registerUser(String sessionId, String username, String location) {
        connectedUsers.put(sessionId, username);
        userLocations.put(sessionId, location != null ? location : "other");
        
        log.info("Usuario conectado: {} en {} (sesión: {})", username, location, sessionId);
        broadcastConnectedUsersCount();
    }

    /**
     * Actualizar la ubicación de un usuario
     */
    public void updateUserLocation(String sessionId, String location) {
        if (connectedUsers.containsKey(sessionId)) {
            userLocations.put(sessionId, location);
            log.debug("Usuario cambió a ubicación: {} (sesión: {})", location, sessionId);
            broadcastConnectedUsersCount();
        }
    }

    /**
     * Desregistrar un usuario desconectado
     */
    public void unregisterUser(String sessionId) {
        String username = connectedUsers.remove(sessionId);
        userLocations.remove(sessionId);
        
        if (username != null) {
            log.info("Usuario desconectado: {} (sesión: {})", username, sessionId);
            broadcastConnectedUsersCount();
        }
    }

    /**
     * Obtener cantidad total de usuarios conectados
     */
    public int getConnectedUsersCount() {
        return connectedUsers.size();
    }

    /**
     * Obtener cantidad de usuarios en dashboard
     */
    public int getDashboardUsersCount() {
        return (int) userLocations.values().stream()
            .filter(location -> "dashboard".equalsIgnoreCase(location))
            .count();
    }

    /**
     * Obtener cantidad de usuarios en otras páginas
     */
    public int getOtherPagesUsersCount() {
        return (int) userLocations.values().stream()
            .filter(location -> !"dashboard".equalsIgnoreCase(location))
            .count();
    }

    /**
     * Obtener mapa de todos los usuarios conectados
     */
    public Map<String, String> getConnectedUsers() {
        return new ConcurrentHashMap<>(connectedUsers);
    }

    /**
     * Obtener mapa de ubicaciones de usuarios
     */
    public Map<String, String> getUserLocations() {
        return new ConcurrentHashMap<>(userLocations);
    }

    /**
     * Broadcast del número de usuarios conectados a todos los clientes
     */
    public void broadcastConnectedUsersCount() {
        int totalCount = getConnectedUsersCount();
        int dashboardCount = getDashboardUsersCount();
        int otherPagesCount = getOtherPagesUsersCount();
        
        // Crear objeto con los contadores
        Map<String, Integer> counts = Map.of(
            "total", totalCount,
            "dashboard", dashboardCount,
            "otherPages", otherPagesCount
        );
        
        messagingTemplate.convertAndSend("/topic/users-count", counts);
        log.debug("Enviando conteo - Total: {}, Dashboard: {}, Otras: {}", 
            totalCount, dashboardCount, otherPagesCount);
    }

    /**
     * Enviar notificación de backup completado
     */
    public void notifyBackupCompleted(String message) {
        messagingTemplate.convertAndSend("/topic/backup-notification", message);
        log.info("Notificación de backup enviada: {}", message);
    }
}
