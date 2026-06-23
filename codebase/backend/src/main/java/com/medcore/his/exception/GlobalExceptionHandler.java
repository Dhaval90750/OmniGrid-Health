package com.medcore.his.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicatePatientException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicatePatientException(DuplicatePatientException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Duplicate Patient Found");
        response.put("message", ex.getMessage());
        response.put("duplicatePatient", ex.getDuplicatePatient());
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
