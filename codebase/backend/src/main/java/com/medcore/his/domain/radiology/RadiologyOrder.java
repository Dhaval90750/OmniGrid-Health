package com.medcore.his.domain.radiology;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.clinical.Visit;
import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "radiology_orders")
@Getter
@Setter
public class RadiologyOrder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column(nullable = false, length = 50)
    private String modality;

    @Column(name = "study_description", nullable = false, length = 255)
    private String studyDescription;

    @Column(name = "clinical_indication", columnDefinition = "TEXT")
    private String clinicalIndication;

    @Column(nullable = false, length = 50)
    private String urgency = "Routine"; // Routine, Urgent, Stat

    @Column(nullable = false, length = 50)
    private String status = "Scheduled"; // Scheduled, Completed, Reported

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;
    
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private RadiologyReport report;
}
