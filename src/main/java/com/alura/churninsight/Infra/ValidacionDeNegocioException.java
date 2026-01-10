package com.alura.churninsight.Infra;

public class ValidacionDeNegocioException extends RuntimeException {
    public ValidacionDeNegocioException(String mensaje) {
        super(mensaje);
    }
}
