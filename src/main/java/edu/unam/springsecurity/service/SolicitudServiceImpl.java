package edu.unam.springsecurity.service;

import edu.unam.springsecurity.enums.EstadoSolicitud;
import edu.unam.springsecurity.enums.MotivoDeclinacion;
import edu.unam.springsecurity.enums.TipoAtencion;
import edu.unam.springsecurity.model.Solicitud;
import edu.unam.springsecurity.model.SolicitudAdjunto;
import edu.unam.springsecurity.repository.SolicitudAdjuntoRepository;
import edu.unam.springsecurity.repository.SolicitudRepository;
import edu.unam.springsecurity.util.Archivos;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

    private static final String DIRECTORIO_SOLICITUDES = "uploads/solicitudes";

    private final SolicitudRepository solicitudRepository;
    private final SolicitudAdjuntoRepository adjuntoRepository;

    @Value("${servitec.solicitudes.ttl-minutes:10}")
    private long tiempoLimiteRespuestaMinutos;

    @Override
    public Solicitud guardar(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }

    @Override
    @Transactional
    public Solicitud guardarConAdjuntos(Solicitud solicitud, MultipartFile[] adjuntos) {
        if (solicitud.getFechaSolicitud() == null) {
            solicitud.setFechaSolicitud(LocalDate.now());
        }
        if (solicitud.getFechaProgramada() == null) {
            solicitud.setFechaProgramada(solicitud.getFechaSolicitud());
        }
        if (solicitud.getFechaLimiteRespuesta() == null) {
            solicitud.setFechaLimiteRespuesta(LocalDateTime.now().plusMinutes(tiempoLimiteRespuestaMinutos));
        }

        Solicitud guardada = solicitudRepository.save(solicitud);
        adjuntarArchivos(guardada, adjuntos);
        return solicitudRepository.save(guardada);
    }

    @Override
    public List<Solicitud> buscarPorUsuario(Integer idUsuario) {
        return solicitudRepository.findByUsuarioId(idUsuario);
    }

    @Override
    public List<Solicitud> buscarPendientesPorTecnico(Integer idTecnico) {
        return solicitudRepository.findByTecnicoIdAndEstadoIn(
                idTecnico,
                Set.of(EstadoSolicitud.PENDIENTE, EstadoSolicitud.REPROGRAMADA)
        );
    }

    @Override
    public List<Solicitud> buscarAgendaPorTecnico(Integer idTecnico) {
        return solicitudRepository.findByTecnicoIdAndEstadoIn(
                idTecnico,
                Set.of(EstadoSolicitud.ACEPTADA, EstadoSolicitud.EN_PROCESO, EstadoSolicitud.REPROGRAMADA)
        );
    }

    @Override
    @Transactional
    public Solicitud aceptarSolicitud(Integer solicitudId, Integer tecnicoId, boolean contactoConfirmado, String observaciones) {
        Solicitud solicitud = solicitudRepository.findByIdAndTecnicoId(solicitudId, tecnicoId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada para este técnico"));

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE && solicitud.getEstado() != EstadoSolicitud.REPROGRAMADA) {
            throw new IllegalStateException("La solicitud no está disponible para ser aceptada");
        }

        solicitud.setEstado(EstadoSolicitud.ACEPTADA);
        solicitud.setObservacionesTecnico(observaciones);
        solicitud.setContactoConfirmado(contactoConfirmado);
        solicitud.setFechaDecision(LocalDateTime.now());
        solicitud.setMotivoDeclinacion(null);
        solicitud.setMotivoDeclinacionDetalle(null);

        if (solicitud.getTipoAtencion() == TipoAtencion.INMEDIATA) {
            solicitud.setFechaProgramada(LocalDate.now());
        }

        return solicitudRepository.save(solicitud);
    }

    @Override
    @Transactional
    public Solicitud declinarSolicitud(Integer solicitudId, Integer tecnicoId, MotivoDeclinacion motivo, String detalle) {
        Solicitud solicitud = solicitudRepository.findByIdAndTecnicoId(solicitudId, tecnicoId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada para este técnico"));

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE && solicitud.getEstado() != EstadoSolicitud.REPROGRAMADA) {
            throw new IllegalStateException("La solicitud no está disponible para ser declinada");
        }

        solicitud.setEstado(EstadoSolicitud.DECLINADA);
        solicitud.setMotivoDeclinacion(motivo);
        solicitud.setMotivoDeclinacionDetalle(detalle);
        solicitud.setObservacionesTecnico(null);
        solicitud.setContactoConfirmado(Boolean.FALSE);
        solicitud.setFechaDecision(LocalDateTime.now());

        return solicitudRepository.save(solicitud);
    }

    private void adjuntarArchivos(Solicitud solicitud, MultipartFile[] adjuntos) {
        if (adjuntos == null || adjuntos.length == 0) {
            return;
        }

        solicitud.getAdjuntos().clear();

        String baseDir = DIRECTORIO_SOLICITUDES + "/" + solicitud.getId();
        int index = 1;
        for (MultipartFile archivo : adjuntos) {
            if (archivo == null || archivo.isEmpty()) {
                continue;
            }
            String extension = Archivos.obtenerExtension(archivo.getOriginalFilename());
            if (extension == null || extension.isBlank()) {
                extension = "dat";
            }
            String nombreFinal = String.format("%d_%d_%d.%s",
                    solicitud.getId(), System.currentTimeMillis(), index++, extension.toLowerCase());

            String rutaAlmacenada = Archivos.almacenarConNombre(archivo, baseDir, nombreFinal);

            SolicitudAdjunto adjunto = new SolicitudAdjunto();
            adjunto.setSolicitud(solicitud);
            adjunto.setNombreArchivo(nombreFinal);
            adjunto.setRutaArchivo("/" + baseDir + "/" + rutaAlmacenada);
            adjunto.setTipoMime(archivo.getContentType());
            adjunto.setTamanioBytes(archivo.getSize());

            solicitud.getAdjuntos().add(adjunto);
        }

        if (!solicitud.getAdjuntos().isEmpty()) {
            adjuntoRepository.saveAll(solicitud.getAdjuntos());
        }
    }
}
