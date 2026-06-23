package com.medcore.his.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientRegistrationRequest {
    
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    @NotNull
    private LocalDate dateOfBirth;
    
    @NotBlank
    private String gender;
    
    @NotBlank
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
    
    private boolean bypassDuplicateCheck = false;
}
