package com.medcore.his.domain.nursing;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.clinical.Prescription;
import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "med_administrations")
@Getter
@Setter
public class MedAdministration extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "administered_by", nullable = false)
    private User administeredBy;

    @Column(name = "administered_at", nullable = false)
    private LocalDateTime administeredAt = LocalDateTime.now();

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(nullable = false, length = 50)
    private String status = "Given"; // Given, Missed, Refused

    @Column(columnDefinition = "TEXT")
    private String notes;
}
