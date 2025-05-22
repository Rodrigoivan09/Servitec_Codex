package edu.unam.springsecurity.security;

import edu.unam.springsecurity.model.Administrador;
import edu.unam.springsecurity.model.Tecnico;
import edu.unam.springsecurity.model.Usuario;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
public class UserDetailsImpl implements UserDetails {

    private Integer id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    // Referencias al usuario original
    private Usuario usuario;
    private Tecnico tecnico;
    private Administrador administrador;

    // Constructor para Usuario
    public UserDetailsImpl(Usuario usuario) {
        this.usuario = usuario;
        this.id = usuario.getId();
        this.email = usuario.getCorreo();
        this.password = usuario.getContrasena();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // Constructor para Técnico
    public UserDetailsImpl(Tecnico tecnico) {
        this.tecnico = tecnico;
        this.id = tecnico.getId();
        this.email = tecnico.getCorreo();
        this.password = tecnico.getContrasena();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_TECNICO"));
    }

    // Constructor para Administrador
    public UserDetailsImpl(Administrador administrador) {
        this.administrador = administrador;
        this.id = administrador.getId();
        this.email = administrador.getCorreo();
        this.password = administrador.getContrasena();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    // Métodos para obtener al usuario original
    public Usuario getUsuario() {
        return usuario;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public Administrador getAdministrador() {
        return administrador;
    }

    public Integer getId() {
        return id;
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
        return email;
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
        return true;
    }
}
