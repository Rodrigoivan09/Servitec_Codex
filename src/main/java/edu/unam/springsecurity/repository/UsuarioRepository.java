package edu.unam.springsecurity.repository;

import edu.unam.springsecurity.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Integer> {
    Usuario findByCorreoAndContrasena(String correo, String contrasena);
    Usuario findByCorreo(String correo);
    Usuario findByTelefono(String telefono);
    List<Usuario> findAll();

}
