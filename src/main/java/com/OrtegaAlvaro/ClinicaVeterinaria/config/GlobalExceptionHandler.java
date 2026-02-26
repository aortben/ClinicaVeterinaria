package com.OrtegaAlvaro.ClinicaVeterinaria.config;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la API REST.
 * Intercepta las excepciones lanzadas por los controladores y devuelve
 * respuestas JSON estructuradas con códigos HTTP apropiados.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Errores de validación (@Valid) → 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 400);
        body.put("error", "Error de validación");

        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
        body.put("errores", errores);

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Violaciones de integridad (UNIQUE constraints) → 409 Conflict
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 409);
        body.put("error", "Conflicto de datos");
        body.put("mensaje", "Operación denegada: Existe un registro con datos duplicados o hay dependencias activas.");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Entidad no encontrada → 404 Not Found
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 404);
        body.put("error", "No encontrado");
        body.put("mensaje", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Errores de transacción (normalmente envuelven ConstraintViolationException) →
     * 400 Bad Request
     */
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, Object>> handleTransaction(TransactionSystemException ex) {
        Throwable cause = ex.getRootCause();
        if (cause instanceof ConstraintViolationException cve) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", 400);
            body.put("error", "Error de validación en entidad");

            Map<String, String> errores = new HashMap<>();
            for (ConstraintViolation<?> v : cve.getConstraintViolations()) {
                errores.put(v.getPropertyPath().toString(), v.getMessage());
            }
            body.put("errores", errores);
            return ResponseEntity.badRequest().body(body);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 500);
        body.put("error", "Error de transacción");
        body.put("mensaje", cause != null ? cause.getMessage() : ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * Cualquier otra excepción no controlada → 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 500);
        body.put("error", "Error interno del servidor");
        body.put("mensaje", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
