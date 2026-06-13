package com.PPOOII.Laboratorio.Services.Interfaces;

import com.PPOOII.Laboratorio.Entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUsuarioService {

    Usuario guardar(Usuario usuario);

    Usuario actualizar(Usuario usuario);

    void eliminar(Long usuarioId);

    Page<Usuario> consultarUsuario(Pageable pageable);

    Usuario getUsuarioById(Long usuarioId);

    Usuario getUsuarioByLoginYPersona(String login, int persona);
}
