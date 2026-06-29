package com.medcore.his.domain.clinical;

import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "triage_assessments")
@Getter
@Setter
public class TriageAssessment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "chief_complaint", length = 500)
    private String chiefComplaint;

    @Column(name = "esi_level", nullable = false)
    private Integer esiLevel; // 1 to 5

    @Column(name = "systolic_bp")
    private Integer systolicBp;

    @Column(name = "diastolic_bp")
    private Integer diastolicBp;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "respiratory_rate")
    private Integer respiratoryRate;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "oxygen_saturation")
    private Integer oxygenSaturation;

    @Column(name = "gcs_score")
    private Integer gcsScore;

    @Column(name = "assessed_by", length = 100)
    private String assessedBy;
    
    @Column(length = 20)
    private String status = "PENDING"; // PENDING, ADMITTED, DISCHARGED
}
