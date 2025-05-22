package edu.unam.springsecurity.service;



import edu.unam.springsecurity.model.PagoSimulado;

import java.util.List;

public interface PagoSimuladoService {
    PagoSimulado guardar(PagoSimulado pago);
    List<PagoSimulado> buscarPorUsuario(Integer idUsuario);
}
