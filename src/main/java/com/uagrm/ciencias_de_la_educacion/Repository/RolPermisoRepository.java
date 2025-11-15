package com.uagrm.ciencias_de_la_educacion.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uagrm.ciencias_de_la_educacion.Model.RolPermisoEntity;

import java.util.List;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermisoEntity, Long> {
    List<RolPermisoEntity> findByRol_Id(Long rolId);
    void deleteByRol_Id(Long rolId);
}
