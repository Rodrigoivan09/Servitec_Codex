package edu.unam.springsecurity.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "usuarios")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @NotBlank
    //@NombreValido
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Email
    @Column(name = "correo", nullable = false, unique = true, length = 100)
    private String correo;

    @NotBlank
    @Pattern(regexp = "\\d{10}", message = "El teléfono debe tener 10 dígitos")
    @Column(name = "telefono", length = 15)
    private String telefono;

    @Column(name="direccion",nullable = false)
    private String direccion;

    @NotBlank
    //@PasswordSegura
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena;
}
