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

    @Column(name = "family_history", columnDefinition = "TEXT")
    private String familyHistory;

    @Column(name = "social_history", columnDefinition = "TEXT")
    private String socialHistory;

    @Column(name = "review_of_systems", columnDefinition = "TEXT")
    private String reviewOfSystems;

    @Column(name = "subjective_notes", columnDefinition = "TEXT")
    private String subjectiveNotes;

    @Column(name = "objective_notes", columnDefinition = "TEXT")
    private String objectiveNotes;

    @Column(name = "assessment_notes", columnDefinition = "TEXT")
    private String assessmentNotes;

    @Column(name = "plan_notes", columnDefinition = "TEXT")
    private String planNotes;

    @Column(name = "treatment_plan", columnDefinition = "TEXT")
    private String treatmentPlan;

    @Column(name = "is_finalized", nullable = false)
    private boolean isFinalized = false;

    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt;

    @Column(name = "is_signed", nullable = false)
    private boolean isSigned = false;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signed_by")
    private User signedBy;

    @Column(name = "addendum_text", columnDefinition = "TEXT")
    private String addendumText;

    @Column(name = "note_version", nullable = false)
    private Integer noteVersion = 1;

    @Column(name = "parent_note_id")
    private java.util.UUID parentNoteId; // for version history, points to the original note if this is a historical copy
}
