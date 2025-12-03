package com.uagrm.ciencias_de_la_educacion.Config;

import com.uagrm.ciencias_de_la_educacion.Service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
public class WebSocketEventListener {

    private final WebSocketService webSocketService;

    public WebSocketEventListener(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        // Obtener el username del header (si está disponible)
        String username = headerAccessor.getFirstNativeHeader("username");
        String location = headerAccessor.getFirstNativeHeader("location");
        
        // Si no hay username, usar "anonymous"
        if (sessionId != null) {
            if (username == null || username.isEmpty()) {
                username = "anonymous";
            }
            if (location == null || location.isEmpty()) {
                location = "other";
            }
            webSocketService.registerUser(sessionId, username, location);
        }
        
        log.info("WebSocket conectado - Sesión: {}, Usuario: {}, Ubicación: {}", sessionId, username, location);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        if (sessionId != null) {
            webSocketService.unregisterUser(sessionId);
        }
        
        log.info("WebSocket desconectado - Sesión: {}", sessionId);
    }
}
