package com.alura.churninsight.Services;

import com.alura.churninsight.Repository.ClienteRepository;
import com.alura.churninsight.domain.Cliente.Cliente;
import com.alura.churninsight.domain.Cliente.GeneroStatus;
import com.alura.churninsight.domain.Cliente.PlanStatus;
import com.alura.churninsight.domain.Prediccion.DatosSolicitudPrediccion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Cliente registrarOActualizar(Integer idCliente, Integer tiempoMeses, Integer retrasosPago,
            Double usoMensualHrs, PlanStatus plan, Integer soporteTickets,
            GeneroStatus genero, Boolean cambioPlan, Boolean pagoAutomatico, Boolean churn) {

        Cliente cliente = repository.findByClienteId(idCliente).orElseGet(Cliente::new);

        cliente.setClienteId(idCliente);
        cliente.setTiempoMeses(tiempoMeses);
        cliente.setRetrasosPago(retrasosPago);
        cliente.setUsoMensualHrs(usoMensualHrs);
        cliente.setPlan(plan);
        cliente.setSoporteTickets(soporteTickets);

        if (genero != null)
            cliente.setGenero(genero);
        else if (cliente.getGenero() == null)
            cliente.setGenero(GeneroStatus.MASCULINO);

        if (cambioPlan != null)
            cliente.setCambioPlan(cambioPlan);
        else if (cliente.getCambioPlan() == null)
            cliente.setCambioPlan(false);

        if (pagoAutomatico != null)
            cliente.setPagoAutomatico(pagoAutomatico);
        else if (cliente.getPagoAutomatico() == null)
            cliente.setPagoAutomatico(false);

        if (churn != null)
            cliente.setChurn(churn);
        else if (cliente.getChurn() == null)
            cliente.setChurn(false);

        return repository.save(cliente);
    }

    @Transactional
    public Cliente registrarDesdeDTO(DatosSolicitudPrediccion datos) {
        return registrarOActualizar(
                datos.idCliente(),
                datos.tiempoContratoMeses(),
                datos.retrasosPago(),
                datos.usoMensual(),
                datos.plan(),
                datos.ticketsSoporte(),
                datos.genero(),
                datos.cambioPlan(),
                datos.pagoAutomatico(),
                datos.churn());
    }
}
