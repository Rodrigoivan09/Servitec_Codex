package edu.unam.springsecurity.service;



import edu.unam.springsecurity.model.Servicio;

import java.util.List;

public interface ServicioService {
    List<Servicio> obtenerTodos();
    Servicio buscarPorId(Integer id);
    Servicio guardar(Servicio servicio);
    void eliminarPorId(Integer id);
}