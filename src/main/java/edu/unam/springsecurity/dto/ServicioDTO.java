package edu.unam.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioDTO {
    private Integer id;
    private String nombreServicio;
    private String descripcion;
    private Double tarifa;

    public ServicioDTO(Integer id, String descripcion, String s) {
    }
}
