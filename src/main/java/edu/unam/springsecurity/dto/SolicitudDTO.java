package edu.unam.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudDTO {
    private Integer idTecnico;
    private Integer idServicio;
    private String direccion;
    private LocalDate fecha;
    private LocalTime horaLlegada;

    // Datos de pago
    private String numeroTarjeta;
    private String fechaVencimiento;
    private String cvv;
}
