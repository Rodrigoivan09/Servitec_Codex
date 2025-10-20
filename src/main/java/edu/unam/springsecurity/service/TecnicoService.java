package edu.unam.springsecurity.service;

import edu.unam.springsecurity.model.Tecnico;
import edu.unam.springsecurity.model.TecnicoDisponibilidad;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public interface TecnicoService {

    void guardar(Tecnico tecnico);
    List<Tecnico> obtenerTodos();
    Tecnico buscarPorId(Integer id);
    void eliminar(Integer id);
    boolean existePorCorreo(String correo);

    @Transactional(readOnly = true)
    List<Tecnico> obtenerTecnicosPorCategoria(String nombreCategoria);

    Object getText();

    Tecnico actualizarDisponibilidadInmediata(Integer tecnicoId, boolean disponible, String notas);

    TecnicoDisponibilidad registrarDisponibilidad(Integer tecnicoId, DayOfWeek dia, LocalTime horaInicio, LocalTime horaFin);

    void actualizarEstadoDisponibilidad(Integer disponibilidadId, Integer tecnicoId, boolean activo);

    void eliminarDisponibilidad(Integer disponibilidadId, Integer tecnicoId);

    List<TecnicoDisponibilidad> listarDisponibilidades(Integer tecnicoId);

    Tecnico buscarTecnicoDisponibleInmediato(Integer servicioId);
}
