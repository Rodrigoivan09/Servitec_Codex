package edu.unam.springsecurity.service;

import edu.unam.springsecurity.model.Tecnico;
import edu.unam.springsecurity.repository.TecnicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TecnicoServiceImpl implements TecnicoService {

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Override
    public void guardar(Tecnico tecnico) {
        tecnicoRepository.save(tecnico);
    }

    @Override
    public List<Tecnico> obtenerTodos() {
        return tecnicoRepository.findAll();
    }

    @Override
    public Tecnico buscarPorId(Integer id) {
        return tecnicoRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Integer id) {
        tecnicoRepository.deleteById(id);
    }

    @Override
    public boolean existePorCorreo(String correo) {
        return tecnicoRepository.existsByCorreo(correo);
    }

    @Override
    public List<Tecnico> obtenerTecnicosPorCategoria(String nombreCategoria) {
        return tecnicoRepository.findByCategoriasNombreCategoria(nombreCategoria);
    }

    @Override
    public Object getText() {
        return null;
    }
}
