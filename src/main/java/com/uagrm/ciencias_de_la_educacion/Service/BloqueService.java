package com.uagrm.ciencias_de_la_educacion.Service;

import com.uagrm.ciencias_de_la_educacion.DTO.BloqueDTO;
import com.uagrm.ciencias_de_la_educacion.DTO.BloqueSaveRequestDTO;
import com.uagrm.ciencias_de_la_educacion.Model.BloqueContenido;
import com.uagrm.ciencias_de_la_educacion.Model.ContenidoEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.BloqueContenidoRepository;
import com.uagrm.ciencias_de_la_educacion.Repository.ContenidoRepository;
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
    private ContenidoRepository contenidoRepo;

    /**
     * Obtener todos los bloques de un Contenido
     */
    @Transactional(readOnly = true)
    public List<BloqueContenido> getBloquesPorContenido(Long contenidoId) {
        return bloqueRepo.findByContenido_IdOrderByOrdenAsc(contenidoId);
    }

    /**
     * Guardar todos los bloques de un Contenido (Delete-then-Insert)
     */
    @Transactional
    public void saveBloquesParaContenido(Long contenidoId, BloqueSaveRequestDTO request) {
        // 1. Busca la entidad Contenido (Padre)
        ContenidoEntity contenido = contenidoRepo.findById(contenidoId)
                .orElseThrow(() -> new EntityNotFoundException("Contenido no encontrado con id: " + contenidoId));

        // 2. BORRA todos los bloques antiguos para este Contenido
        bloqueRepo.deleteByContenido_Id(contenidoId);

        // 3. CREA los nuevos bloques desde el DTO
        List<BloqueContenido> nuevosBloques = new ArrayList<>();

        for (BloqueDTO dto : request.getBloques()) {
            BloqueContenido nuevoBloque = new BloqueContenido();
            nuevoBloque.setContenido(contenido); // Enlaza al Contenido
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
