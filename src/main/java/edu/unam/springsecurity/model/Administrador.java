package edu.unam.springsecurity.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Administradores")
public class Administrador implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_admin")
    private Integer id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(name = "correo", nullable = false, unique = true)
    private String correo;

    @NotBlank
    @Pattern(regexp = "\\d{10}", message = "El teléfono debe tener 10 dígitos")
    @Column(name = "telefono", nullable = false, length = 15)
    private String telefono;

    @NotBlank
    @Size(min = 8)
    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena;
}

