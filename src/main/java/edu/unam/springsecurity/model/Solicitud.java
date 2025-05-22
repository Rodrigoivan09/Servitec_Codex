package edu.unam.springsecurity.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "Solicitudes")
@Data
@NoArgsConstructor @AllArgsConstructor
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_tecnico", nullable = false)
    private Tecnico tecnico;

    @ManyToOne
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;

    private LocalDate fechaSolicitud;
    private LocalTime horaLlegada;

    @Column(nullable = false)
    private String estado = "Pendiente";

    @OneToOne(mappedBy = "solicitud", cascade = CascadeType.ALL)
    private PagoSimulado pago;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private LocalDate fecha;

}
