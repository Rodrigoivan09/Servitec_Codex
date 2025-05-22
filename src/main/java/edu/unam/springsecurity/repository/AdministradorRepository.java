package edu.unam.springsecurity.repository;


import edu.unam.springsecurity.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {

    boolean existsByCorreo(String correo);
    Administrador findByCorreo(String correo);
    Administrador findByTelefono(String telefono);

}
