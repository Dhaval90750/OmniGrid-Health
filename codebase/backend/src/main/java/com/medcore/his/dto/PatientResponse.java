package com.medcore.his.dto;

import com.medcore.his.domain.patient.Patient;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class PatientResponse {
    private UUID id;
    private String uhid;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String mobileNumber;
    private String bloodGroup;
    private String email;
    private String addressLine1;
    private String city;
    private String state;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String qrCodeBase64;

    public static PatientResponse fromEntity(Patient patient, String qrCodeBase64) {
        PatientResponse dto = new PatientResponse();
        dto.setId(patient.getId());
        dto.setUhid(patient.getUhid());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setGender(patient.getGender());
        dto.setMobileNumber(patient.getMobileNumber());
        dto.setBloodGroup(patient.getBloodGroup());
        dto.setEmail(patient.getEmail());
        dto.setAddressLine1(patient.getAddressLine1());
        dto.setCity(patient.getCity());
        dto.setState(patient.getState());
        dto.setEmergencyContactName(patient.getEmergencyContactName());
        dto.setEmergencyContactPhone(patient.getEmergencyContactPhone());
        dto.setQrCodeBase64(qrCodeBase64);
        return dto;
    }
}
