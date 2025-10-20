package edu.unam.springsecurity.service;

import edu.unam.springsecurity.model.Usuario;
import edu.unam.springsecurity.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{


	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public void guardar(Usuario usuario) {
		usuarioRepository.save(usuario);

	}

	@Override
	public List<Usuario> obtenerTodos() {
        return Collections.emptyList();
	}

	@Override
	public Usuario buscarPorId(Integer id) {
		usuarioRepository.findById(id);
		return null;
	}

	@Override
	public void eliminar(Integer id) {

	}

	@Override
	public boolean existePorCorreo(String correo) {
		return false;
	}

	@Override
	public Usuario validarLogin(String correo, String contrasena) {
		return null;
	}

	@Override
	public Usuario buscarPorCorreoOTelefono(String input) {
		Usuario usuario = usuarioRepository.findByCorreo(input);
		if (usuario == null && input.matches("\\d{10}")) {
			usuario = usuarioRepository.findByTelefono(input);
		}
		return usuario;
	}

	@Override
	public String getText() {
		return "";
	}
}
