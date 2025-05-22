package edu.unam.springsecurity.service;

import edu.unam.springsecurity.model.Servicio;
import edu.unam.springsecurity.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioServiceImpl implements ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Override
    public List<Servicio> obtenerTodos() {
        return servicioRepository.findAll();
    }

    @Override
    public Servicio buscarPorId(Integer id) {
        return servicioRepository.findById(id).orElse(null);
    }

    @Override
    public Servicio guardar(Servicio servicio) {
        return servicioRepository.save(servicio);
    }

    @Override
    public void eliminarPorId(Integer id) {
        servicioRepository.deleteById(id);
    }
}