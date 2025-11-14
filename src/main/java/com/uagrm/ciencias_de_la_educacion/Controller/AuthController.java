package com.uagrm.ciencias_de_la_educacion.Controller;

import com.uagrm.ciencias_de_la_educacion.DTO.*;
import com.uagrm.ciencias_de_la_educacion.Model.RefreshToken;
import com.uagrm.ciencias_de_la_educacion.Model.UsuarioEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.UsuarioRepository;
import com.uagrm.ciencias_de_la_educacion.Security.JwtTokenProvider;
import com.uagrm.ciencias_de_la_educacion.Security.UserDetailsImpl;
import com.uagrm.ciencias_de_la_educacion.Service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getCorreo(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Crear refresh token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    refreshToken.getToken(),
                    userDetails.getId(),
                    userDetails.getCorreo(),
                    userDetails.getNombre(),
                    userDetails.getApellido(),
                    roles
            ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Error: Credenciales inválidas"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUsuario)
                .map(usuario -> {
                    UserDetailsImpl userDetails = UserDetailsImpl.build(usuario);
                    
                    // Crear autenticación temporal para generar el nuevo token
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    String token = jwtTokenProvider.generateToken(authentication);
                    
                    List<String> roles = userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList());

                    return ResponseEntity.ok(new JwtResponse(
                            token,
                            requestRefreshToken,
                            userDetails.getId(),
                            userDetails.getCorreo(),
                            userDetails.getNombre(),
                            userDetails.getApellido(),
                            roles
                    ));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token no encontrado"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        if (usuarioRepository.existsByCorreo(registerRequest.getCorreo())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El correo ya está registrado"));
        }

        // Crear nuevo usuario
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre(registerRequest.getNombre());
        usuario.setApellido(registerRequest.getApellido());
        usuario.setCorreo(registerRequest.getCorreo());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setEstado(true);

        // Asignar roles
        if (registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty()) {
            usuario.setRoles(new HashSet<>());
            usuario.getRoles().add("USER");
        } else {
            usuario.setRoles(registerRequest.getRoles());
        }

        usuarioRepository.save(usuario);

        return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente"));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(
                    null,
                    null,
                    userDetails.getId(),
                    userDetails.getCorreo(),
                    userDetails.getNombre(),
                    userDetails.getApellido(),
                    roles
            ));
        }
        
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Token inválido o expirado"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody(required = false) RefreshTokenRequest request) {
        if (request != null && request.getRefreshToken() != null) {
            refreshTokenService.deleteByToken(request.getRefreshToken());
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Logout exitoso"));
    }
}

