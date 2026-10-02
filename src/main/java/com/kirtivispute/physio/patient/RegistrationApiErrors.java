package com.kirtivispute.physio.patient;

import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.TreeMap;

@RestControllerAdvice(assignableTypes = RegistrationApiController.class)
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
        return ResponseEntity.badRequest().body(Map.of("error", "INVALID_REQUEST", "message", "Send a valid JSON registration request."));
    }
}
