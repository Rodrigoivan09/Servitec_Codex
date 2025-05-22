package edu.unam.springsecurity.repository;

import edu.unam.springsecurity.model.Administrador;
import edu.unam.springsecurity.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}
