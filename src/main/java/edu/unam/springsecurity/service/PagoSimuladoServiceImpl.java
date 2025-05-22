package edu.unam.springsecurity.service;


import edu.unam.springsecurity.model.PagoSimulado;
import edu.unam.springsecurity.repository.PagoSimuladoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagoSimuladoServiceImpl implements PagoSimuladoService {

    @Autowired
    private PagoSimuladoRepository pagoSimuladoRepository;

    @Override
    public PagoSimulado guardar(PagoSimulado pago) {
        return pagoSimuladoRepository.save(pago);
    }

    @Override
    public List<PagoSimulado> buscarPorUsuario(Integer idUsuario) {
        return pagoSimuladoRepository.findBySolicitudUsuarioId(idUsuario);
    }
}
