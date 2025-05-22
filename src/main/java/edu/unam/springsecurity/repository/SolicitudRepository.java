package edu.unam.springsecurity.repository;

import edu.unam.springsecurity.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {
    List<Solicitud> findByUsuarioId(Integer idUsuario);
}
