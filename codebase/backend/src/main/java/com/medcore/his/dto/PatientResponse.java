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
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String maritalStatus;
    private String nationality;
    private String primaryLanguage;
    private String nationalId;
    private String emergencyContactName;
    private String emergencyContactRelation;
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
        dto.setAddressLine2(patient.getAddressLine2());
        dto.setCity(patient.getCity());
        dto.setState(patient.getState());
        dto.setCountry(patient.getCountry());
        dto.setZipCode(patient.getZipCode());
        dto.setMaritalStatus(patient.getMaritalStatus());
        dto.setNationality(patient.getNationality());
        dto.setPrimaryLanguage(patient.getPrimaryLanguage());
        dto.setNationalId(patient.getNationalId());
        dto.setEmergencyContactName(patient.getEmergencyContactName());
        dto.setEmergencyContactRelation(patient.getEmergencyContactRelation());
        dto.setEmergencyContactPhone(patient.getEmergencyContactPhone());
        dto.setQrCodeBase64(qrCodeBase64);
        return dto;
    }
}
