package com.medcore.his.domain.patient;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
public class Patient extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String uhid;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, length = 20)
    private String gender;

    @Column(name = "blood_group", length = 10)
    private String bloodGroup;

    @Column(name = "marital_status", length = 50)
    private String maritalStatus;

    @Column(length = 50)
    private String nationality;

    @Column(name = "primary_language", length = 50)
    private String primaryLanguage;

    @Column(name = "national_id", unique = true, length = 50)
    private String nationalId;

    @Column(name = "mobile_number", nullable = false, length = 20)
    private String mobileNumber;

    @Column(length = 150)
    private String email;

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String country;

    @Column(name = "zip_code", length = 20)
    private String zipCode;

    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_relation", length = 50)
    private String emergencyContactRelation;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
