package br.com.ibmec.gerenciador_clientes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ApiError error = new ApiError();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setError("Resource Not Found");
        error.setMessage(ex.getMessage());
        error.setPath(request.getRequestURI());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
        ApiError error = new ApiError();
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setError("Duplicate Resource");
        error.setMessage(ex.getMessage());
        error.setPath(request.getRequestURI());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // Método para tratar outras exceções gerais
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception ex, HttpServletRequest request) {
        ApiError error = new ApiError();
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setError("Internal Server Error");
        error.setMessage("Ocorreu um erro inesperado.");
        error.setPath(request.getRequestURI());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
