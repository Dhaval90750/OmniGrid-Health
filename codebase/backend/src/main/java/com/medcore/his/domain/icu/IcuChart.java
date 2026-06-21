package com.medcore.his.domain.icu;

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
@Table(name = "icu_charting")
@Getter
@Setter
public class IcuChart extends BaseEntity {

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

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "blood_pressure_systolic")
    private Integer bloodPressureSystolic;

    @Column(name = "blood_pressure_diastolic")
    private Integer bloodPressureDiastolic;

    @Column(name = "mean_arterial_pressure")
    private Integer meanArterialPressure;

    @Column(precision = 5, scale = 2)
    private BigDecimal temperature;

    private Integer spo2;

    @Column(name = "ventilator_mode", length = 50)
    private String ventilatorMode;

    @Column(precision = 5, scale = 2)
    private BigDecimal fio2;

    @Column(precision = 5, scale = 2)
    private BigDecimal peep;

    @Column(name = "intake_volume")
    private Integer intakeVolume;

    @Column(name = "output_volume")
    private Integer outputVolume;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
