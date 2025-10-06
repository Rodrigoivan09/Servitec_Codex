package edu.unam.springsecurity.model;

import edu.unam.springsecurity.enums.EstadoTecnico;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"categorias", "evaluaciones", "servicios"})

@Entity(name = "tecnicos")
public class Tecnico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tecnico")
    private Integer id;

    //@NombreValido
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    //@CorreoValido
    @Email
    @Column(name = "correo", nullable = false, unique = true, length = 100)
    private String correo;

    //@TelefonoValido
    @Column(name = "telefono", nullable = false, length = 15)
    private String telefono;

    @NotBlank
    @Column(name = "direccion", nullable = false, length = 50)
    private String direccion;

    @Column(name = "efirma")
    private String rutaEfirma;

    @Column(name = "certificacion")
    private String rutaCertificacion;

    @Column(name = "foto_perfil")
    private String rutaFotoPerfil;

    //@PasswordSegura
    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena;

    // Relación: Técnico ↔ Categorías
    @ManyToMany
    @JoinTable(
            name = "tecnico_categoria",
            joinColumns = @JoinColumn(name = "id_tecnico"),
            inverseJoinColumns = @JoinColumn(name = "id_categoria")
    )
    private List<Categoria> categorias;

    // Relación: Técnico ↔ Evaluaciones
    @OneToMany(mappedBy = "tecnico", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Evaluacion> evaluaciones;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tecnico_servicio",
            joinColumns = @JoinColumn(name = "id_tecnico"),
            inverseJoinColumns = @JoinColumn(name = "id_servicio")
    )
    private List<Servicio> servicios;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoTecnico estado = EstadoTecnico.DISPONIBLE;


}
