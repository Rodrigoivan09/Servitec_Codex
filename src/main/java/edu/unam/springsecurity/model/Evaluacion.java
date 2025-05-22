
package edu.unam.springsecurity.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "tecnico")

@Entity
@Table(name = "Evaluaciones")
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Integer id;

    @Column(nullable = false)
    private Integer calificacion; // Entre 1 y 5

    @Column(columnDefinition = "TEXT")
    private String comentarios;

    @Column(name = "fecha_evaluacion")
    private LocalDateTime fechaEvaluacion;

    // Relación con Técnico
    @ManyToOne
    @JoinColumn(name = "id_tecnico", nullable = false)
    private Tecnico tecnico;

    // (Opcional) Relación con Usuario
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // (Opcional) Relación con Servicio
    @ManyToOne
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;
}
