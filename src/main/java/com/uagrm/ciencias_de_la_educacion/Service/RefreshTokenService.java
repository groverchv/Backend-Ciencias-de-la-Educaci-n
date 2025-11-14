package com.uagrm.ciencias_de_la_educacion.Service;

import com.uagrm.ciencias_de_la_educacion.Config.JwtConfig;
import com.uagrm.ciencias_de_la_educacion.Model.RefreshToken;
import com.uagrm.ciencias_de_la_educacion.Model.UsuarioEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.RefreshTokenRepository;
import com.uagrm.ciencias_de_la_educacion.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtConfig jwtConfig;

    public RefreshToken createRefreshToken(Long usuarioId) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Eliminar refresh token anterior si existe
        refreshTokenRepository.findByUsuario(usuario)
                .ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedDate(Instant.now());
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtConfig.getRefresh().getExpiration().getMs()));

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expirado. Por favor, inicie sesión nuevamente.");
        }
        return token;
    }

    @Transactional
    public void deleteByUsuarioId(Long usuarioId) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        refreshTokenRepository.deleteByUsuario(usuario);
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }
}
