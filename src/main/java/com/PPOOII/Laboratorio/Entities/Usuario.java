package com.PPOOII.Laboratorio.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity(name = "UsrANDPer")
@Table(name = "usuario")
@IdClass(UsuarioPK.class)
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "login", nullable = false, unique = true, length = 60)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "apikey", nullable = false, unique = true, length = 120)
    private String apikey;

    @Column(name = "persona_id")
    private Integer personaId;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "persona", nullable = false)
    private Persona persona;

    public Usuario() {
    }

    public Usuario(String login, String password, String apikey, Persona persona) {
        this.login = login;
        this.password = password;
        this.apikey = apikey;
        setPersona(persona);
    }

    @PrePersist
    @PreUpdate
    public void syncPersonaId() {
        if (persona != null) {
            personaId = persona.getId();
        }
    }

    public Long getId() {
        return persona == null ? null : Long.valueOf(persona.getId());
    }

    public void setId(Long id) {
        this.personaId = id == null ? null : id.intValue();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
        this.personaId = persona == null ? null : persona.getId();
    }
}
