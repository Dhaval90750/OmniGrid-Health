package com.medcore.his.domain.nursing;

import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nursing_assessments")
@Getter
@Setter
public class NursingAssessment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false, length = 100)
    private String type; // e.g. "Braden Scale", "Morse Fall Scale"

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false, length = 50)
    private String riskLevel; // e.g. "High Risk", "Moderate Risk"

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(length = 100)
    private String recordedBy;
}
