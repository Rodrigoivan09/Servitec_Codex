package edu.unam.springsecurity.service;


import edu.unam.springsecurity.model.Usuario;

import java.util.List;

public interface UserService {
    void guardar(Usuario usuario);
    List<Usuario> obtenerTodos();
    Usuario buscarPorId(Integer id);
    void eliminar(Integer id);
    boolean existePorCorreo(String correo);
    Usuario validarLogin(String correo, String contrasena);
    Usuario buscarPorCorreoOTelefono (String input);
    public default String getText() {
        return "User";
    }
}