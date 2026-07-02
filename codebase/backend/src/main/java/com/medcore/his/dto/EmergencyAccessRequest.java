package com.medcore.his.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class EmergencyAccessRequest {

    private UUID patientId;

    @NotBlank(message = "Justification type is required")
    private String justificationType;

    @NotBlank(message = "Justification text is required")
    private String justificationText;

    // Getters and Setters
    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public String getJustificationType() {
        return justificationType;
    }

    public void setJustificationType(String justificationType) {
        this.justificationType = justificationType;
    }

    public String getJustificationText() {
        return justificationText;
    }

    public void setJustificationText(String justificationText) {
        this.justificationText = justificationText;
    }
}
