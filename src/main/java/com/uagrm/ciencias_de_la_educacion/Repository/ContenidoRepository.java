package com.uagrm.ciencias_de_la_educacion.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uagrm.ciencias_de_la_educacion.Model.ContenidoEntity;

@Repository
public interface ContenidoRepository extends JpaRepository<ContenidoEntity, Long> {
    
}
