package com.medcore.his.exception;

import com.medcore.his.dto.PatientResponse;
import lombok.Getter;

@Getter
public class DuplicatePatientException extends RuntimeException {
    private final PatientResponse duplicatePatient;

    public DuplicatePatientException(String message, PatientResponse duplicatePatient) {
        super(message);
        this.duplicatePatient = duplicatePatient;
    }
}
