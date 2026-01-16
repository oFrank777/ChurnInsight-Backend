package com.alura.churninsight.domain.Prediccion;

import java.util.List;

public record DatosResultadoIA(double probabilidad, long clase, List<String> factores) {
}
