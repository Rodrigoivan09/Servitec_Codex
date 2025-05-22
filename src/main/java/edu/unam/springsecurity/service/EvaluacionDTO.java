package edu.unam.springsecurity.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionDTO {
    private Integer calificacion;
    private String comentarios;
    private LocalDateTime fechaEvaluacion;
}
