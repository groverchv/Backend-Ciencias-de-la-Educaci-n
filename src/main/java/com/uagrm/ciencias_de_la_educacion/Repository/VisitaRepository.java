package com.uagrm.ciencias_de_la_educacion.Repository;

import com.uagrm.ciencias_de_la_educacion.Model.VisitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitaRepository extends JpaRepository<VisitaEntity, Long> {

    // Count visits by content ID
    long countByContenidoId(Long contenidoId);

    // Count visits in a date range
    @Query("SELECT COUNT(v) FROM VisitaEntity v WHERE v.fechaVisita BETWEEN :startDate AND :endDate")
    long countByFechaVisitaBetween(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Get all visits in a date range
    @Query("SELECT v FROM VisitaEntity v WHERE v.fechaVisita BETWEEN :startDate AND :endDate ORDER BY v.fechaVisita DESC")
    List<VisitaEntity> findByFechaVisitaBetween(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Get visits by content in a date range
    @Query("SELECT v FROM VisitaEntity v WHERE v.contenido.id = :contenidoId AND v.fechaVisita BETWEEN :startDate AND :endDate")
    List<VisitaEntity> findByContenidoIdAndFechaVisitaBetween(
            @Param("contenidoId") Long contenidoId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Count visits grouped by date (for charts)
    @Query("SELECT DATE(v.fechaVisita) as fecha, COUNT(v) as total " +
            "FROM VisitaEntity v " +
            "WHERE v.fechaVisita BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(v.fechaVisita) " +
            "ORDER BY DATE(v.fechaVisita)")
    List<Object[]> countVisitsByDateBetween(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Get top visited content in a date range
    @Query("SELECT v.contenido.id, v.contenido.titulo, COUNT(v) as total " +
            "FROM VisitaEntity v " +
            "WHERE v.fechaVisita BETWEEN :startDate AND :endDate " +
            "GROUP BY v.contenido.id, v.contenido.titulo " +
            "ORDER BY COUNT(v) DESC")
    List<Object[]> findTopVisitedContent(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
