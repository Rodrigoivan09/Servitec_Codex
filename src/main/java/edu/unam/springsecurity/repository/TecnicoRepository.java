package edu.unam.springsecurity.repository;

import edu.unam.springsecurity.enums.EstadoTecnico;
import edu.unam.springsecurity.model.Tecnico;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Integer> {
    boolean existsByCorreo(String correo);

    Tecnico findByCorreo(String correo);
    Tecnico findByTelefono(String telefono);
    List<Tecnico> findByCategoriasNombreCategoria(String nombreCategoria);
    List<Tecnico> findByDisponibleAhoraTrue();

    @Query("SELECT t FROM Tecnico t " +
            "JOIN t.servicios s " +
            "LEFT JOIN t.evaluaciones e " +
            "WHERE s.id = :servicioId " +
            "AND t.disponibleAhora = true " +
            "AND t.estado = :estado " +
            "GROUP BY t " +
            "ORDER BY COALESCE(AVG(e.calificacion), 0) DESC")
    List<Tecnico> findDisponiblesPorServicioOrderByCalificacion(@Param("servicioId") Integer servicioId,
                                                                @Param("estado") EstadoTecnico estado);
}
