package edu.unam.springsecurity.repository;


import edu.unam.springsecurity.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioRepository extends JpaRepository<Servicio, Integer> {
}