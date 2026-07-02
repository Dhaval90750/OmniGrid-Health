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
    private String photoBase64;
    private String middleName;
    private String occupation;
    private String secondaryMobile;
    private String abhaId;
    private String passportNumber;
    private String religion;
    private String referredBy;

    // Generated QR code for patient card (optional, only populated in some endpoints)
    private String qrCodeBase64;

    public static PatientResponse fromEntity(Patient patient, String qrCodeBase64) {
        if (patient == null) return null;
        
        PatientResponse response = new PatientResponse();
        response.setId(patient.getId());
        response.setUhid(patient.getUhid());
        response.setFirstName(patient.getFirstName());
        response.setLastName(patient.getLastName());
        response.setDateOfBirth(patient.getDateOfBirth());
        response.setGender(patient.getGender());
        response.setMobileNumber(patient.getMobileNumber());
        response.setBloodGroup(patient.getBloodGroup());
        response.setEmail(patient.getEmail());
        response.setAddressLine1(patient.getAddressLine1());
        response.setAddressLine2(patient.getAddressLine2());
        response.setCity(patient.getCity());
        response.setState(patient.getState());
        response.setCountry(patient.getCountry());
        response.setZipCode(patient.getZipCode());
        response.setMaritalStatus(patient.getMaritalStatus());
        response.setNationality(patient.getNationality());
        response.setPrimaryLanguage(patient.getPrimaryLanguage());
        response.setNationalId(patient.getNationalId());
        response.setEmergencyContactName(patient.getEmergencyContactName());
        response.setEmergencyContactRelation(patient.getEmergencyContactRelation());
        response.setEmergencyContactPhone(patient.getEmergencyContactPhone());
        
        response.setMiddleName(patient.getMiddleName());
        response.setOccupation(patient.getOccupation());
        response.setSecondaryMobile(patient.getSecondaryMobile());
        response.setAbhaId(patient.getAbhaId());
        response.setPassportNumber(patient.getPassportNumber());
        response.setReligion(patient.getReligion());
        response.setReferredBy(patient.getReferredBy());
        response.setPhotoBase64(patient.getPhoto());
        
        response.setQrCodeBase64(qrCodeBase64);
        
        return response;
    }
}
