package edu.unam.springsecurity.dto;

import edu.unam.springsecurity.enums.EstadoTecnico;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TecnicoDTO {
    private Integer id;
    private String nombre;
    private String correo;
    private String telefono;
    private Double promedioCalificacion;
    private List<EvaluacionDTO> evaluaciones;
    private List<ServicioDTO> servicios;
    private EstadoTecnico estado;

}
