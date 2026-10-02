package com.kirtivispute.physio.patient;

import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.TreeMap;
import com.kirtivispute.physio.appointment.PortalException;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Order(0)
@RestControllerAdvice(annotations = RestController.class)
public class RegistrationApiErrors {
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<?> duplicate(DuplicateEmailException failure) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "EMAIL_IN_USE", "message", failure.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> invalid(MethodArgumentNotValidException failure) {
        Map<String, String> fields = new TreeMap<>();
        failure.getBindingResult().getFieldErrors().forEach(error -> fields.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("error", "VALIDATION_FAILED",
                "message", "Please correct the highlighted fields.", "fields", fields));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> unreadable() {
        return ResponseEntity.badRequest().body(Map.of("error", "INVALID_REQUEST", "message", "Send a valid JSON request."));
    }

    @ExceptionHandler(PortalException.class)
    public ResponseEntity<?> expected(PortalException failure) {
        return ResponseEntity.status(failure.getStatus()).body(Map.of("error", failure.getCode(), "message", failure.getMessage()));
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> badLogin() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "INVALID_CREDENTIALS", "message", "Invalid email or password."));
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> invalidId() {
        return ResponseEntity.badRequest().body(Map.of("error", "INVALID_REQUEST", "message", "Use a valid numeric identifier."));
    }
}
