package com.uagrm.ciencias_de_la_educacion.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uagrm.ciencias_de_la_educacion.Model.RolUsuarioEntity;

import java.util.List;

@Repository
public interface RolUsuarioRepository extends JpaRepository<RolUsuarioEntity, Long> {
    List<RolUsuarioEntity> findByUsuario_Id(Long usuarioId);
    void deleteByUsuario_Id(Long usuarioId);
}
