package com.medcore.his.domain.patient;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "patient_allergies")
@Getter
@Setter
public class PatientAllergy extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false, length = 255)
    private String allergen;

    @Column(nullable = false, length = 50)
    private String severity;

    @Column(length = 255)
    private String reaction;

    @Column(name = "identified_date")
    private LocalDate identifiedDate;

    @Column(nullable = false, length = 50)
    private String status = "ACTIVE";

    @Column(name = "verified_by", length = 100)
    private String verifiedBy;
}
