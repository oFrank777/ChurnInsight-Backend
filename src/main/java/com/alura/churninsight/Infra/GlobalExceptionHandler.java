package com.alura.churninsight.Infra;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import jakarta.persistence.EntityNotFoundException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tratarError404() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ValidacionDeNegocioException.class)
    public ResponseEntity tratarErrorDeValidacion(ValidacionDeNegocioException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity tratarErrorDeTipo(MethodArgumentTypeMismatchException e) {
        String message = String.format("El parámetro '%s' tiene un valor inválido o fuera de rango.", e.getName());
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity tratarError500(Exception e) {
        return ResponseEntity.internalServerError()
                .body(Map.of("message", "Error interno: " + e.getLocalizedMessage()));
    }
}
