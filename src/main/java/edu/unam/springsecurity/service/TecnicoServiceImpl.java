package edu.unam.springsecurity.service;

import edu.unam.springsecurity.enums.EstadoTecnico;
import edu.unam.springsecurity.model.Tecnico;
import edu.unam.springsecurity.model.TecnicoDisponibilidad;
import edu.unam.springsecurity.repository.TecnicoDisponibilidadRepository;
import edu.unam.springsecurity.repository.TecnicoRepository;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TecnicoServiceImpl implements TecnicoService {

    private final TecnicoRepository tecnicoRepository;
    private final TecnicoDisponibilidadRepository disponibilidadRepository;

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

    @Override
    @Transactional
    public Tecnico actualizarDisponibilidadInmediata(Integer tecnicoId, boolean disponible, String notas) {
        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
                .orElseThrow(() -> new IllegalArgumentException("Técnico no encontrado"));
        tecnico.setDisponibleAhora(disponible);
        tecnico.setNotasDisponibilidad(notas);
        return tecnicoRepository.save(tecnico);
    }

    @Override
    @Transactional
    public TecnicoDisponibilidad registrarDisponibilidad(Integer tecnicoId, DayOfWeek dia, LocalTime horaInicio, LocalTime horaFin) {
        if (horaFin.isBefore(horaInicio) || horaFin.equals(horaInicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }
        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
                .orElseThrow(() -> new IllegalArgumentException("Técnico no encontrado"));

        TecnicoDisponibilidad disponibilidad = new TecnicoDisponibilidad();
        disponibilidad.setTecnico(tecnico);
        disponibilidad.setDia(dia);
        disponibilidad.setHoraInicio(horaInicio);
        disponibilidad.setHoraFin(horaFin);
        disponibilidad.setActivo(Boolean.TRUE);
        return disponibilidadRepository.save(disponibilidad);
    }

    @Override
    @Transactional
    public void actualizarEstadoDisponibilidad(Integer disponibilidadId, Integer tecnicoId, boolean activo) {
        TecnicoDisponibilidad disponibilidad = disponibilidadRepository.findById(disponibilidadId)
                .orElseThrow(() -> new IllegalArgumentException("Disponibilidad no encontrada"));
        if (!disponibilidad.getTecnico().getId().equals(tecnicoId)) {
            throw new IllegalArgumentException("No autorizado para modificar esta disponibilidad");
        }
        disponibilidad.setActivo(activo);
        disponibilidadRepository.save(disponibilidad);
    }

    @Override
    @Transactional
    public void eliminarDisponibilidad(Integer disponibilidadId, Integer tecnicoId) {
        TecnicoDisponibilidad disponibilidad = disponibilidadRepository.findById(disponibilidadId)
                .orElseThrow(() -> new IllegalArgumentException("Disponibilidad no encontrada"));
        if (!disponibilidad.getTecnico().getId().equals(tecnicoId)) {
            throw new IllegalArgumentException("No autorizado para eliminar esta disponibilidad");
        }
        disponibilidadRepository.delete(disponibilidad);
    }

    @Override
    public List<TecnicoDisponibilidad> listarDisponibilidades(Integer tecnicoId) {
        return disponibilidadRepository.findByTecnicoId(tecnicoId);
    }

    @Override
    public Tecnico buscarTecnicoDisponibleInmediato(Integer servicioId) {
        List<Tecnico> disponibles = tecnicoRepository.findDisponiblesPorServicioOrderByCalificacion(servicioId, EstadoTecnico.DISPONIBLE);
        return disponibles.isEmpty() ? null : disponibles.get(0);
    }
}
