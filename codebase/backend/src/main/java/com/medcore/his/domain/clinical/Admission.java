package com.medcore.his.domain.clinical;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.master.Bed;
import com.medcore.his.domain.master.Ward;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "admissions")
@Getter
@Setter
public class Admission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admitting_doctor_id", nullable = false)
    private User admittingDoctor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bed_id", nullable = false)
    private Bed bed;

    @Column(nullable = false, length = 50)
    private String status = "ADMITTED";

    @Column(name = "admission_reason", nullable = false, columnDefinition = "TEXT")
    private String admissionReason;

    @Column(name = "admission_type", length = 50)
    private String admissionType; // Emergency, Elective, Transfer

    @Column(name = "room_type", length = 50)
    private String roomType;

    @Column(length = 100)
    private String insurance;

    @Column(name = "pre_auth_number", length = 50)
    private String preAuthNumber;

    @Column(name = "consent_for_treatment")
    private Boolean consentForTreatment = false;

    @Column(name = "mlc_flag")
    private Boolean mlcFlag = false;

    @Column(name = "provisional_diagnosis", length = 255)
    private String provisionalDiagnosis;

    @Column(name = "admission_date", nullable = false)
    private LocalDateTime admissionDate = LocalDateTime.now();

    @Column(name = "discharge_date")
    private LocalDateTime dischargeDate;

    @Column(name = "discharge_summary", columnDefinition = "TEXT")
    private String dischargeSummary;
}
