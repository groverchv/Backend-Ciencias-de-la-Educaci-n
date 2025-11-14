package com.uagrm.ciencias_de_la_educacion.Repository;

import com.uagrm.ciencias_de_la_educacion.Model.RefreshToken;
import com.uagrm.ciencias_de_la_educacion.Model.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUsuario(UsuarioEntity usuario);
    Optional<RefreshToken> findByUsuario(UsuarioEntity usuario);
}
