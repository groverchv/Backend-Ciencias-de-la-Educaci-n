package com.uagrm.ciencias_de_la_educacion.Service;

import com.uagrm.ciencias_de_la_educacion.Model.ContenidoEntity;
import com.uagrm.ciencias_de_la_educacion.Model.VisitaEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.ContenidoRepository;
import com.uagrm.ciencias_de_la_educacion.Repository.VisitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class VisitaService {

    @Autowired
    private VisitaRepository visitaRepository;

    @Autowired
    private ContenidoRepository contenidoRepository;

    // Register a new visit
    public VisitaEntity registerVisit(Long contenidoId, String ipAddress) {
        Optional<ContenidoEntity> contenidoOpt = contenidoRepository.findById(contenidoId);
        if (contenidoOpt.isEmpty()) {
            throw new RuntimeException("Contenido no encontrado con ID: " + contenidoId);
        }

        VisitaEntity visita = new VisitaEntity();
        visita.setContenido(contenidoOpt.get());
        visita.setIpAddress(ipAddress);
        // fechaVisita is set automatically in @PrePersist

        return visitaRepository.save(visita);
    }

    // Get total visits count
    public long getTotalVisits() {
        return visitaRepository.count();
    }

    // Get visits by period
    public long getVisitsByPeriod(String period) {
        LocalDateTime[] dateRange = getDateRange(period);
        return visitaRepository.countByFechaVisitaBetween(dateRange[0], dateRange[1]);
    }

    // Get visit trends for charts (grouped by date)
    public List<Map<String, Object>> getVisitTrends(String period) {
        LocalDateTime[] dateRange = getDateRange(period);
        List<Object[]> results = visitaRepository.countVisitsByDateBetween(dateRange[0], dateRange[1]);

        List<Map<String, Object>> trends = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("fecha", result[0].toString());
            dataPoint.put("visitas", result[1]);
            trends.add(dataPoint);
        }

        return trends;
    }

    // Get top visited content
    public List<Map<String, Object>> getTopVisitedContent(String period, int limit) {
        LocalDateTime[] dateRange = getDateRange(period);
        List<Object[]> results = visitaRepository.findTopVisitedContent(dateRange[0], dateRange[1]);

        List<Map<String, Object>> topContent = new ArrayList<>();
        int count = 0;
        for (Object[] result : results) {
            if (count >= limit)
                break;

            Map<String, Object> item = new HashMap<>();
            item.put("contenidoId", result[0]);
            item.put("titulo", result[1]);
            item.put("visitas", result[2]);
            topContent.add(item);
            count++;
        }

        return topContent;
    }

    // Get visits by content ID
    public long getVisitsByContenido(Long contenidoId) {
        return visitaRepository.countByContenidoId(contenidoId);
    }

    // Get visits by content ID and period
    public long getVisitsByContenidoAndPeriod(Long contenidoId, String period) {
        LocalDateTime[] dateRange = getDateRange(period);
        return visitaRepository.findByContenidoIdAndFechaVisitaBetween(
                contenidoId, dateRange[0], dateRange[1]).size();
    }

    // Helper method to calculate date range based on period
    private LocalDateTime[] getDateRange(String period) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate;

        switch (period.toLowerCase()) {
            case "dia":
            case "day":
                startDate = LocalDate.now().atStartOfDay();
                break;
            case "semana":
            case "week":
                startDate = LocalDate.now().minus(7, ChronoUnit.DAYS).atStartOfDay();
                break;
            case "mes":
            case "month":
                startDate = LocalDate.now().minus(30, ChronoUnit.DAYS).atStartOfDay();
                break;
            case "año":
            case "year":
                startDate = LocalDate.now().minus(365, ChronoUnit.DAYS).atStartOfDay();
                break;
            default:
                // Default to month
                startDate = LocalDate.now().minus(30, ChronoUnit.DAYS).atStartOfDay();
        }

        return new LocalDateTime[] { startDate, endDate };
    }
}
