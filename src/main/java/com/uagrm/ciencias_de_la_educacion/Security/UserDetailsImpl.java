package com.uagrm.ciencias_de_la_educacion.Security;

import com.uagrm.ciencias_de_la_educacion.Model.RolUsuarioEntity;
import com.uagrm.ciencias_de_la_educacion.Model.UsuarioEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.RolUsuarioRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
    private String password;
    private Boolean estado;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(UsuarioEntity usuario, RolUsuarioRepository rolUsuarioRepository) {
        List<RolUsuarioEntity> rolUsuarios = rolUsuarioRepository.findByUsuarioId(usuario.getId());

        Collection<GrantedAuthority> authorities = rolUsuarios.stream()
                .map(rolUsuario -> new SimpleGrantedAuthority("ROLE_" + rolUsuario.getRol().getNombre()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreo(),
                usuario.getPassword(),
                usuario.getEstado(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return correo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return estado != null && estado;
    }
}
