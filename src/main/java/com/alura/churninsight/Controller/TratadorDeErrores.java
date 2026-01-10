package com.alura.churninsight.Controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import com.alura.churninsight.Infra.ValidacionDeNegocioException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class TratadorDeErrores {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> tratoError404(EntityNotFoundException e) {
        return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<java.util.List<Map<String, String>>> tratoError400(MethodArgumentNotValidException e) {
        var errores = e.getFieldErrors().stream()
                .map(err -> Map.of("campo", err.getField(), "error", err.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> tratoErrorRecursoNoEncontrado() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ValidacionDeNegocioException.class)
    public ResponseEntity<Map<String, String>> tratoErrorDeValidacion(ValidacionDeNegocioException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> tratoError500(Exception e) {
        log.error("ERROR INTERNO DETECTADO: {}", e.getMessage(), e);
        return ResponseEntity.status(500).body(Map.of("message", "Error interno: " + e.getMessage()));
    }
}
