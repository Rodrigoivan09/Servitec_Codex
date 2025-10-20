package edu.unam.springsecurity.service;

import edu.unam.springsecurity.enums.MotivoDeclinacion;
import edu.unam.springsecurity.model.Solicitud;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface SolicitudService {
    Solicitud guardar(Solicitud solicitud);
    Solicitud guardarConAdjuntos(Solicitud solicitud, MultipartFile[] adjuntos);
    List<Solicitud> buscarPorUsuario(Integer idUsuario);
    List<Solicitud> buscarPendientesPorTecnico(Integer idTecnico);
    List<Solicitud> buscarAgendaPorTecnico(Integer idTecnico);
    Solicitud aceptarSolicitud(Integer solicitudId, Integer tecnicoId, boolean contactoConfirmado, String observaciones);
    Solicitud declinarSolicitud(Integer solicitudId, Integer tecnicoId, MotivoDeclinacion motivo, String detalle);
}
