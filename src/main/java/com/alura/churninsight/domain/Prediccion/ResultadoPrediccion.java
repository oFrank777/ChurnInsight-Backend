package com.alura.churninsight.domain.Prediccion;

import java.util.List;
import com.alura.churninsight.domain.Cliente.PlanStatus;
import com.alura.churninsight.domain.Cliente.GeneroStatus;

public record ResultadoPrediccion(
        boolean churn, // Nuevo campo solicitado por el equipo
        String prevision,
        double probabilidad,
        List<String> factores,
        Integer idCliente,
        GeneroStatus genero,
        PlanStatus plan,
        Integer tiempoContratoMeses,
        Double usoMensual,
        Integer ticketsSoporte,
        Integer retrasosPago,
        Boolean pagoAutomatico,
        Boolean cambioPlan) {
}
