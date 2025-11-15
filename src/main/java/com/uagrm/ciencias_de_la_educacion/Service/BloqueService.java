package com.uagrm.ciencias_de_la_educacion.Service;

import com.uagrm.ciencias_de_la_educacion.DTO.BloqueDTO;
import com.uagrm.ciencias_de_la_educacion.DTO.BloqueSaveRequestDTO;
import com.uagrm.ciencias_de_la_educacion.Model.BloqueContenido;
import com.uagrm.ciencias_de_la_educacion.Model.Sub_MenuEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.BloqueContenidoRepository;
import com.uagrm.ciencias_de_la_educacion.Repository.Sub_MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BloqueService {

    @Autowired
    private BloqueContenidoRepository bloqueRepo;

    @Autowired
    private Sub_MenuRepository subMenuRepo;

    /**
     * Endpoint 1: Obtener todos los bloques de una página
     */
    @Transactional(readOnly = true)
    public List<BloqueContenido> getBloquesPorSubMenu(Long subMenuId) {
        return bloqueRepo.findBySubMenu_IdOrderByOrdenAsc(subMenuId);
    }

    /**
     * Endpoint 2: Guardar todos los bloques de una página (Delete-then-Insert)
     */
    @Transactional
    public void saveBloquesParaSubMenu(Long subMenuId, BloqueSaveRequestDTO request) {
        // 1. Busca la entidad SubMenu (Padre)
        Sub_MenuEntity subMenu = subMenuRepo.findById(subMenuId)
                .orElseThrow(() -> new EntityNotFoundException("SubMenu no encontrado con id: " + subMenuId));

        // 2. BORRA todos los bloques antiguos para este SubMenu
        bloqueRepo.deleteBySubMenu_Id(subMenuId);

        // 3. CREA los nuevos bloques desde el DTO
        List<BloqueContenido> nuevosBloques = new ArrayList<>();

        for (BloqueDTO dto : request.getBloques()) {
            BloqueContenido nuevoBloque = new BloqueContenido();
            nuevoBloque.setSubMenu(subMenu); // Enlaza al padre
            nuevoBloque.setOrden(dto.getOrden());
            nuevoBloque.setTipoBloque(dto.getTipoBloque());
            nuevoBloque.setDatosJson(dto.getDatosJson());
            nuevoBloque.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
            
            nuevosBloques.add(nuevoBloque);
        }

        // 4. Guarda todos los nuevos bloques en lote
        bloqueRepo.saveAll(nuevosBloques);
    }
}
