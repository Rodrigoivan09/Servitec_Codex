package edu.unam.springsecurity.repository;

import edu.unam.springsecurity.model.Categoria;
import edu.unam.springsecurity.model.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarifaRepository extends JpaRepository<Tarifa, Integer> {
}
