package com.PPOOII.Laboratorio.Services;

import com.PPOOII.Laboratorio.Entities.Usuario;
import com.PPOOII.Laboratorio.Repository.UsuarioRepository;
import com.PPOOII.Laboratorio.Services.Interfaces.IUsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service("jwtInMemoryUserDetailsService")
public class UsuarioServiceImpl implements IUsuarioService, UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(Objects.requireNonNull(usuario, "usuario no puede ser null"));
    }

    @Override
    public Usuario actualizar(Usuario usuario) {
        Usuario usuarioValidado = Objects.requireNonNull(usuario, "usuario no puede ser null");
        String login = Objects.requireNonNull(usuarioValidado.getLogin(), "login no puede ser null");
        int personaId = Objects.requireNonNull(usuarioValidado.getPersona(), "persona no puede ser null").getId();
        getUsuarioByLoginYPersona(login, personaId);
        return usuarioRepository.save(usuarioValidado);
    }

    @Override
    public void eliminar(Long usuarioId) {
        Usuario usuario = Objects.requireNonNull(getUsuarioById(usuarioId), "usuario no puede ser null");
        usuarioRepository.delete(usuario);
    }

    @Override
    public Page<Usuario> consultarUsuario(Pageable pageable) {
        Pageable pageableValidado = Objects.requireNonNull(pageable, "pageable no puede ser null");
        return usuarioRepository.findAll(pageableValidado);
    }

    @Override
    public Usuario getUsuarioById(Long usuarioId) {
        Long idValidado = Objects.requireNonNull(usuarioId, "usuarioId no puede ser null");
        return usuarioRepository.findByPersonaId(idValidado.intValue())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con persona " + idValidado));
    }

    @Override
    public Usuario getUsuarioByLoginYPersona(String login, int persona) {
        String loginValidado = Objects.requireNonNull(login, "login no puede ser null");
        return usuarioRepository.getUsuarioANDPersona(loginValidado, persona)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Usuario no encontrado con login " + loginValidado + " y persona " + persona
            ));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String login = Objects.requireNonNull(username, "username no puede ser null");
        Usuario usuario = usuarioRepository.findByUsername(login)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con login " + login));

        return User.withUsername(usuario.getLogin())
            .password(usuario.getPassword())
            .authorities(AuthorityUtils.NO_AUTHORITIES)
            .build();
    }
}
