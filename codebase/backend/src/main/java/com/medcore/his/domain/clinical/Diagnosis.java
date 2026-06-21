package com.medcore.his.domain.clinical;

import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "diagnoses")
@Getter
@Setter
public class Diagnosis extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    private Visit visit;

    @Column(name = "diagnosis_name", nullable = false, length = 255)
    private String diagnosisName;

    @Column(name = "icd10_code", length = 20)
    private String icd10Code;

    @Column(nullable = false, length = 50)
    private String type = "Provisional"; // Provisional, Confirmed, Differential

    @Column(nullable = false, length = 50)
    private String status = "Active"; // Active, Resolved

    @Column(name = "diagnosed_date", nullable = false)
    private LocalDateTime diagnosedDate = LocalDateTime.now();
}
