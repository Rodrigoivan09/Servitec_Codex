package edu.unam.springsecurity.service;


import edu.unam.springsecurity.model.Solicitud;

import java.util.List;

public interface SolicitudService {
    Solicitud guardar(Solicitud solicitud);
    List<Solicitud> buscarPorUsuario(Integer idUsuario);

}
