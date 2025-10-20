package edu.unam.springsecurity.service;

import edu.unam.springsecurity.model.Administrador;
import edu.unam.springsecurity.model.Usuario;
import edu.unam.springsecurity.model.Tecnico;
import edu.unam.springsecurity.security.UserDetailsImpl;
import edu.unam.springsecurity.repository.AdministradorRepository;
import edu.unam.springsecurity.repository.UsuarioRepository;
import edu.unam.springsecurity.repository.TecnicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        String safeInput = sanitizeForLog(input);
        log.debug("[UDS] loadUserByUsername input='{}'", safeInput);
        // Buscar por correo o teléfono en Administradores
        Administrador admin = administradorRepository.findByCorreo(input);
        if (admin == null && input.matches("\\d{10}")) {
            admin = administradorRepository.findByTelefono(input);
        }
        if (admin != null) {
            log.debug("[UDS] encontrado ADMIN '{}'", sanitizeForLog(admin.getCorreo()));
            return new UserDetailsImpl(admin);
        }

        // Buscar por correo o teléfono en Usuarios
        Usuario usuario = usuarioRepository.findByCorreo(input);
        if (usuario == null && input.matches("\\d{10}")) {
            usuario = usuarioRepository.findByTelefono(input);
        }
        if (usuario != null) {
            log.debug("[UDS] encontrado USER '{}'", sanitizeForLog(usuario.getCorreo()));
            return new UserDetailsImpl(usuario);
        }

        // Buscar por correo o teléfono en Técnicos
        Tecnico tecnico = tecnicoRepository.findByCorreo(input);
        if (tecnico == null && input.matches("\\d{10}")) {
            tecnico = tecnicoRepository.findByTelefono(input);
        }
        if (tecnico != null) {
            log.debug("[UDS] encontrado TECNICO '{}'", sanitizeForLog(tecnico.getCorreo()));
            return new UserDetailsImpl(tecnico);
        }
        log.warn("[UDS] no encontrado: '{}'", safeInput);
        throw new UsernameNotFoundException("No se encontró ningún usuario, técnico o administrador con: " + input);
    }

    private String sanitizeForLog(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\r", "").replace("\n", "").trim();
    }
}
