package com.medcore.his.domain.staff;

import com.medcore.his.domain.common.BaseEntity;
import com.medcore.his.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cross_consultations")
@Getter
@Setter
public class CrossConsultation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requesting_doctor_id", nullable = false)
    private StaffProfile requestingDoctor;

    @Column(name = "target_department", nullable = false, length = 100)
    private String targetDepartment;

    @Column(name = "reason_for_consult", nullable = false, columnDefinition = "TEXT")
    private String reasonForConsult;

    @Column(nullable = false, length = 50)
    private String status = "Requested"; // Requested, Accepted, Completed

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulting_doctor_id")
    private StaffProfile consultingDoctor;

    @Column(name = "consultation_notes", columnDefinition = "TEXT")
    private String consultationNotes;
}
