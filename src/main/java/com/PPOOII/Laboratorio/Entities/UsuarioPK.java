package com.PPOOII.Laboratorio.Entities;

import java.io.Serializable;
import java.util.Objects;

public class UsuarioPK implements Serializable {

    private static final long serialVersionUID = 1L;

    private String login;
    private Integer persona;

    public UsuarioPK() {
    }

    public UsuarioPK(String login, Integer persona) {
        this.login = login;
        this.persona = persona;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer getPersona() {
        return persona;
    }

    public void setPersona(Integer persona) {
        this.persona = persona;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UsuarioPK usuarioPK)) {
            return false;
        }
        return Objects.equals(login, usuarioPK.login)
            && Objects.equals(persona, usuarioPK.persona);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, persona);
    }
}
