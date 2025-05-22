package edu.unam.springsecurity.repository;

import edu.unam.springsecurity.model.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Integer> {
    boolean existsByCorreo(String correo);

    Tecnico findByCorreo(String correo);
    Tecnico findByTelefono(String telefono);
    List<Tecnico> findByCategoriasNombreCategoria(String nombreCategoria);
}
