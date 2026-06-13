package com.PPOOII.Laboratorio.Repository;

import com.PPOOII.Laboratorio.Entities.Usuario;
import com.PPOOII.Laboratorio.Entities.UsuarioPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, UsuarioPK> {

    @Query("SELECT u FROM UsrANDPer u WHERE u.login = :login AND u.persona.id = :persona")
    Optional<Usuario> getUsuarioANDPersona(@Param("login") String login, @Param("persona") int persona);

    @Query("SELECT u FROM UsrANDPer u WHERE u.login = :login")
    Optional<Usuario> findByUsername(@Param("login") String login);

    @Query("SELECT u FROM UsrANDPer u WHERE u.persona.id = :persona")
    Optional<Usuario> findByPersonaId(@Param("persona") int persona);
}
