package com.alura.churninsight.Services;

import java.util.List;

@org.springframework.stereotype.Service
public class ModeloChurnService {

    public ResultadoModelo calcularProbabilidadCancelacion(com.alura.churninsight.domain.Cliente.Cliente cliente) {
        double probabilidad = 0.0;
        java.util.List<String> factores = new java.util.ArrayList<>();

        if (cliente.getRetrasosPago() > 1) {
            probabilidad += 0.30;
            factores.add("Múltiples retrasos en pagos registrados");
        }
        if (cliente.getSoporteTickets() > 4) {
            probabilidad += 0.25;
            factores.add("Excesiva interacción con soporte técnico");
        }
        if (cliente.getUsoMensualHrs() < 5) {
            probabilidad += 0.20;
            factores.add("Abandono de uso del servicio (Inactividad)");
        }
        if (cliente.getTiempoMeses() != null && cliente.getTiempoMeses() < 12) {
            probabilidad += 0.15;
            factores.add("Cliente en periodo crítico de retención (<1 año)");
        }
        if ("BASICO".equals(cliente.getPlan().name())) {
            probabilidad += 0.10;
            factores.add("Plan con baja fidelización (Básico)");
        }

        // Cambio a Boolean: false o null es riesgo
        if (cliente.getPagoAutomatico() == null || !cliente.getPagoAutomatico()) {
            probabilidad += 0.10;
            factores.add("Método de pago manual (Riesgo de olvido)");
        }

        // Cambio a Boolean: true es riesgo de inestabilidad
        if (Boolean.TRUE.equals(cliente.getCambioPlan())) {
            probabilidad += 0.15;
            factores.add("Inactividad tras cambio de plan reciente");
        }

        java.util.List<String> mejoresFactores = factores.stream().limit(3).toList();

        return new ResultadoModelo(Math.min(probabilidad, 1.0), mejoresFactores);
    }

    public record ResultadoModelo(double probabilidad, List<String> factores) {
    }
}
