package com.uagrm.ciencias_de_la_educacion.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uagrm.ciencias_de_la_educacion.Model.PermisoEntity;

import java.util.Optional;

@Repository
public interface PermisoRepository extends JpaRepository<PermisoEntity, Long> {
    Optional<PermisoEntity> findByNombre(String nombre);
    Boolean existsByNombre(String nombre);
}
