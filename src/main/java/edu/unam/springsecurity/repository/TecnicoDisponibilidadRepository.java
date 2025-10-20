package edu.unam.springsecurity.repository;

import edu.unam.springsecurity.model.TecnicoDisponibilidad;
import java.time.DayOfWeek;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TecnicoDisponibilidadRepository extends JpaRepository<TecnicoDisponibilidad, Integer> {
    List<TecnicoDisponibilidad> findByTecnicoId(Integer tecnicoId);
    List<TecnicoDisponibilidad> findByTecnicoIdAndDia(Integer tecnicoId, DayOfWeek dia);
}
