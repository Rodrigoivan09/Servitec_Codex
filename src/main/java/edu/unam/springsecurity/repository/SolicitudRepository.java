package edu.unam.springsecurity.repository;

import edu.unam.springsecurity.enums.EstadoSolicitud;
import edu.unam.springsecurity.enums.TipoAtencion;
import edu.unam.springsecurity.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {
    List<Solicitud> findByUsuarioId(Integer idUsuario);
    List<Solicitud> findByTecnicoId(Integer idTecnico);
    List<Solicitud> findByTecnicoIdAndEstadoIn(Integer idTecnico, Collection<EstadoSolicitud> estados);
    Optional<Solicitud> findByIdAndTecnicoId(Integer id, Integer idTecnico);
    List<Solicitud> findByEstadoAndTipoAtencion(EstadoSolicitud estado, TipoAtencion tipoAtencion);
}
