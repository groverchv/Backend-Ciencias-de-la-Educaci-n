package com.uagrm.ciencias_de_la_educacion.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uagrm.ciencias_de_la_educacion.Model.TextoEntity;

@Repository
public interface TextoRepository extends JpaRepository<TextoEntity, Long> {
    
}
