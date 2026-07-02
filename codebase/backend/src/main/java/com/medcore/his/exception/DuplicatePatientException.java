package com.medcore.his.exception;

import com.medcore.his.dto.PatientResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class DuplicatePatientException extends RuntimeException {
    private final List<PatientResponse> duplicatePatients;

    public DuplicatePatientException(String message, List<PatientResponse> duplicatePatients) {
        super(message);
        this.duplicatePatients = duplicatePatients;
    }
}
