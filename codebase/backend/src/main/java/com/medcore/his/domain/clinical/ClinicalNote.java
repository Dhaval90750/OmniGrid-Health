package com.medcore.his.domain.clinical;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "clinical_notes")
@Getter
@Setter
public class ClinicalNote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column(name = "note_type", nullable = false, length = 50)
    private String noteType = "OPD_CONSULT";

    @Column(name = "history_of_present_illness", columnDefinition = "TEXT")
    private String historyOfPresentIllness;

    @Column(name = "past_medical_history", columnDefinition = "TEXT")
    private String pastMedicalHistory;

    @Column(name = "physical_examination", columnDefinition = "TEXT")
    private String physicalExamination;

    @Column(name = "treatment_plan", columnDefinition = "TEXT")
    private String treatmentPlan;

    @Column(name = "is_finalized", nullable = false)
    private boolean isFinalized = false;

    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt;
}
