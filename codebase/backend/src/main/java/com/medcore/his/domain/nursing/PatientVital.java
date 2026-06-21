package com.medcore.his.domain.nursing;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.clinical.Visit;
import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_vitals")
@Getter
@Setter
public class PatientVital extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recorded_by", nullable = false)
    private User recordedBy;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt = LocalDateTime.now();

    @Column(precision = 5, scale = 2)
    private BigDecimal temperature;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "blood_pressure_systolic")
    private Integer bloodPressureSystolic;

    @Column(name = "blood_pressure_diastolic")
    private Integer bloodPressureDiastolic;

    @Column(name = "respiratory_rate")
    private Integer respiratoryRate;

    private Integer spo2;

    @Column(name = "pain_score")
    private Integer painScore;

    @Column(name = "news_score")
    private Integer newsScore;
}
