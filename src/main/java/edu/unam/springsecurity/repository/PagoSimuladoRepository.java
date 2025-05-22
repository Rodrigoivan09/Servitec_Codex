package edu.unam.springsecurity.repository;

import edu.unam.springsecurity.model.PagoSimulado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoSimuladoRepository extends JpaRepository<PagoSimulado, Integer> {
    List<PagoSimulado> findBySolicitudUsuarioId(Integer idUsuario);


}
