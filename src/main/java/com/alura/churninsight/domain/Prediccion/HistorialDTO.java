package com.alura.churninsight.domain.Prediccion;

import java.time.LocalDateTime;
import java.util.List;

public record HistorialDTO(
                Long id,
                Integer idCliente,
                double probabilidad,
                String resultado,
                boolean churn, // Nuevo campo
                LocalDateTime fecha,
                List<String> factores,
                String plan) {
}
