package edu.unam.springsecurity.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "Pagos_Simulados")
@Data
@NoArgsConstructor @AllArgsConstructor
public class PagoSimulado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "id_solicitud", nullable = false, unique = true)
    private Solicitud solicitud;

    @Column(nullable = false, length = 16)
    private String numeroTarjeta;

    @Column(nullable = false)
    private String fechaVencimiento;

    @Column(nullable = false)
    private Integer cvv;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
}
