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
     * GET /api/sub_menu/{id}/bloques
     * Obtiene todos los bloques de una página (SubMenu)
     */
    @GetMapping("/sub_menu/{id}/bloques")
    public ResponseEntity<List<BloqueContenido>> getBloquesPorSubMenu(@PathVariable("id") Long subMenuId) {
        List<BloqueContenido> bloques = bloqueService.getBloquesPorSubMenu(subMenuId);
        return ResponseEntity.ok(bloques);
    }

    /**
     * POST /api/sub_menu/{id}/bloques
     * Guarda (sobrescribe) todos los bloques de una página (SubMenu)
     */
    @PostMapping("/sub_menu/{id}/bloques")
    public ResponseEntity<Void> saveBloques(
            @PathVariable("id") Long subMenuId,
            @RequestBody BloqueSaveRequestDTO request
    ) {
        bloqueService.saveBloquesParaSubMenu(subMenuId, request);
        return ResponseEntity.ok().build();
    }
}
