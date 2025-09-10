package edu.unam.springsecurity.service;

import edu.unam.springsecurity.model.Administrador;
import edu.unam.springsecurity.model.Usuario;
import edu.unam.springsecurity.model.Tecnico;
import edu.unam.springsecurity.security.UserDetailsImpl;
import edu.unam.springsecurity.repository.AdministradorRepository;
import edu.unam.springsecurity.repository.UsuarioRepository;
import edu.unam.springsecurity.repository.TecnicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        // Buscar por correo o teléfono en Administradores
        Administrador admin = administradorRepository.findByCorreo(input);
        if (admin == null && input.matches("\\d{10}")) {
            admin = administradorRepository.findByTelefono(input);
        }
        if (admin != null) {
            return new UserDetailsImpl(admin);
        }

        // Buscar por correo o teléfono en Usuarios
        Usuario usuario = usuarioRepository.findByCorreo(input);
        if (usuario == null && input.matches("\\d{10}")) {
            usuario = usuarioRepository.findByTelefono(input);
        }
        if (usuario != null) {
            return new UserDetailsImpl(usuario);
        }

        // Buscar por correo o teléfono en Técnicos
        Tecnico tecnico = tecnicoRepository.findByCorreo(input);
        if (tecnico == null && input.matches("\\d{10}")) {
            tecnico = tecnicoRepository.findByTelefono(input);
        }
        if (tecnico != null) {
            return new UserDetailsImpl(tecnico);
        }

        throw new UsernameNotFoundException("No se encontró ningún usuario, técnico o administrador con: " + input);
    }
}
