package com.uagrm.ciencias_de_la_educacion.Controller;

import com.uagrm.ciencias_de_la_educacion.Service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/websocket")
@Slf4j
public class WebSocketController {

    private final WebSocketService webSocketService;

    public WebSocketController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    /**
     * Endpoint REST para obtener usuarios conectados
     */
    @GetMapping("/connected-users")
    public ResponseEntity<Map<String, Object>> getConnectedUsers() {
        Map<String, Object> response = new HashMap<>();
        response.put("total", webSocketService.getConnectedUsersCount());
        response.put("dashboard", webSocketService.getDashboardUsersCount());
        response.put("otherPages", webSocketService.getOtherPagesUsersCount());
        response.put("allUsers", webSocketService.getConnectedUsers());
        response.put("locations", webSocketService.getUserLocations());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint REST para obtener solo el conteo total
     */
    @GetMapping("/connected-users/count")
    public ResponseEntity<Map<String, Integer>> getConnectedUsersCount() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("total", webSocketService.getConnectedUsersCount());
        counts.put("dashboard", webSocketService.getDashboardUsersCount());
        counts.put("otherPages", webSocketService.getOtherPagesUsersCount());
        return ResponseEntity.ok(counts);
    }
}

@Controller
@Slf4j
class WebSocketMessageController {

    private final WebSocketService webSocketService;

    public WebSocketMessageController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    /**
     * Manejar registro de usuario cuando se conecta
     */
    @MessageMapping("/user.register")
    public void registerUser(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String username = payload.get("username");
        String location = payload.get("location");
        
        if (sessionId != null && username != null) {
            webSocketService.registerUser(sessionId, username, location);
            log.info("Usuario registrado vía mensaje: {} en {} (sesión: {})", username, location, sessionId);
        }
    }

    /**
     * Manejar cambio de ubicación de usuario
     */
    @MessageMapping("/user.location")
    public void updateUserLocation(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String location = payload.get("location");
        
        if (sessionId != null && location != null) {
            webSocketService.updateUserLocation(sessionId, location);
            log.info("Usuario cambió ubicación a: {} (sesión: {})", location, sessionId);
        }
    }
}
