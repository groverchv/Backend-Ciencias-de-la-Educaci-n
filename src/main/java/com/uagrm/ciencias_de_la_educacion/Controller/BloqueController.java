package com.uagrm.ciencias_de_la_educacion.Controller;

import com.uagrm.ciencias_de_la_educacion.DTO.BloqueSaveRequestDTO;
import com.uagrm.ciencias_de_la_educacion.Model.BloqueContenido;
import com.uagrm.ciencias_de_la_educacion.Service.BloqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BloqueController {

    @Autowired
    private BloqueService bloqueService;

    /**
     * GET /api/contenido/{id}/bloques
     * Obtiene todos los bloques de un Contenido específico
     */
    @GetMapping("/contenido/{id}/bloques")
    public ResponseEntity<List<BloqueContenido>> getBloquesByContenido(@PathVariable("id") Long contenidoId) {
        List<BloqueContenido> bloques = bloqueService.getBloquesPorContenido(contenidoId);
        return ResponseEntity.ok(bloques);
    }

    /**
     * POST /api/contenido/{id}/bloques
     * Guarda todos los bloques de un Contenido (reemplaza los existentes)
     */
    @PostMapping("/contenido/{id}/bloques")
    public ResponseEntity<String> saveBloques(
            @PathVariable("id") Long contenidoId,
            @RequestBody BloqueSaveRequestDTO request) {
        bloqueService.saveBloquesParaContenido(contenidoId, request);
        return ResponseEntity.ok("Bloques guardados exitosamente");
    }
}
