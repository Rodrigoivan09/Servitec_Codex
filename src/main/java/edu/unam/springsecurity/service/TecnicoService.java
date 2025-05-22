package edu.unam.springsecurity.service;

import edu.unam.springsecurity.model.Tecnico;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public interface TecnicoService {

    void guardar(Tecnico tecnico);
    List<Tecnico> obtenerTodos();
    Tecnico buscarPorId(Integer id);
    void eliminar(Integer id);
    boolean existePorCorreo(String correo);

    @Transactional(readOnly = true)
    List<Tecnico> obtenerTecnicosPorCategoria(String nombreCategoria);

    Object getText();
}