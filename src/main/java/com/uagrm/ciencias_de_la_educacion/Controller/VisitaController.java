package com.uagrm.ciencias_de_la_educacion.Controller;

import com.uagrm.ciencias_de_la_educacion.Model.VisitaEntity;
import com.uagrm.ciencias_de_la_educacion.Service.VisitaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visitas")
@CrossOrigin(origins = "*")
public class VisitaController {

    @Autowired
    private VisitaService visitaService;

    // Register a new visit
    @PostMapping
    public ResponseEntity<?> registerVisit(
            @RequestParam Long contenidoId,
            HttpServletRequest request) {
        try {
            String ipAddress = getClientIP(request);
            VisitaEntity visita = visitaService.registerVisit(contenidoId, ipAddress);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Visita registrada exitosamente");
            response.put("visitaId", visita.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al registrar visita: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Get visit statistics
    @GetMapping("/stats")
    public ResponseEntity<?> getVisitStats(@RequestParam(defaultValue = "mes") String period) {
        try {
            long totalVisits = visitaService.getVisitsByPeriod(period);
            List<Map<String, Object>> trends = visitaService.getVisitTrends(period);
            List<Map<String, Object>> topContent = visitaService.getTopVisitedContent(period, 10);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalVisitas", totalVisits);
            stats.put("periodo", period);
            stats.put("tendencias", trends);
            stats.put("contenidoMasVisto", topContent);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Get visits by content ID
    @GetMapping("/contenido/{contenidoId}")
    public ResponseEntity<?> getVisitsByContenido(
            @PathVariable Long contenidoId,
            @RequestParam(defaultValue = "mes") String period) {
        try {
            long visits = visitaService.getVisitsByContenidoAndPeriod(contenidoId, period);

            Map<String, Object> response = new HashMap<>();
            response.put("contenidoId", contenidoId);
            response.put("visitas", visits);
            response.put("periodo", period);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener visitas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Helper method to get client IP address
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
