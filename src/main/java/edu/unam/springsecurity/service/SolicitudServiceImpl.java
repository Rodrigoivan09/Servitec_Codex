package edu.unam.springsecurity.service;

import edu.unam.springsecurity.model.Solicitud;
import edu.unam.springsecurity.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Override
    public Solicitud guardar(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }

    @Override
    public List<Solicitud> buscarPorUsuario(Integer idUsuario) {
        return solicitudRepository.findByUsuarioId(idUsuario);
    }
}
