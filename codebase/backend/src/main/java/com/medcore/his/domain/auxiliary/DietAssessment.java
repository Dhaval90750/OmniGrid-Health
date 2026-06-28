package com.medcore.his.domain.auxiliary;

import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "diet_assessments")
@Getter
@Setter
public class DietAssessment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private LocalDate assessmentDate;

    @Column(nullable = false)
    private java.math.BigDecimal weightKg;

    @Column(nullable = false)
    private java.math.BigDecimal heightCm;

    @Column(nullable = false)
    private java.math.BigDecimal bmi;

    @Column(columnDefinition = "TEXT")
    private String nutritionalRisk; // LOW, MODERATE, HIGH

    @Column(columnDefinition = "TEXT")
    private String dietaryPreferences;
}
