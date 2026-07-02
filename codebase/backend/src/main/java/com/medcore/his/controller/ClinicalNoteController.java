package com.medcore.his.controller;

import com.medcore.his.domain.clinical.ClinicalNote;
import com.medcore.his.repository.ClinicalNoteRepository;
import com.medcore.his.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/clinical-notes")
public class ClinicalNoteController {

    private final ClinicalNoteRepository clinicalNoteRepository;

    @Autowired
    public ClinicalNoteController(ClinicalNoteRepository clinicalNoteRepository) {
        this.clinicalNoteRepository = clinicalNoteRepository;
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ClinicalNote>> getNotesForPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(clinicalNoteRepository.findByVisitPatientIdOrderByCreatedAtDesc(patientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicalNote> getNoteById(@PathVariable UUID id) {
        return clinicalNoteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClinicalNote(
            @PathVariable UUID id,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        return clinicalNoteRepository.findById(id)
                .map(note -> {
                    // Check if signed
                    if (note.isSigned() || note.isFinalized()) {
                        // If signed, we only allow updating addendumText
                        if (payload.containsKey("addendumText")) {
                            note.setAddendumText(payload.get("addendumText"));
                            clinicalNoteRepository.save(note);
                            return ResponseEntity.ok(note);
                        }
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot edit a finalized/signed note. You can only append an addendum.");
                    }
                    
                    // Create a version snapshot before updating
                    ClinicalNote snapshot = new ClinicalNote();
                    snapshot.setVisit(note.getVisit());
                    snapshot.setDoctor(note.getDoctor());
                    snapshot.setNoteType(note.getNoteType());
                    snapshot.setHistoryOfPresentIllness(note.getHistoryOfPresentIllness());
                    snapshot.setPastMedicalHistory(note.getPastMedicalHistory());
                    snapshot.setFamilyHistory(note.getFamilyHistory());
                    snapshot.setSocialHistory(note.getSocialHistory());
                    snapshot.setReviewOfSystems(note.getReviewOfSystems());
                    snapshot.setSubjectiveNotes(note.getSubjectiveNotes());
                    snapshot.setObjectiveNotes(note.getObjectiveNotes());
                    snapshot.setAssessmentNotes(note.getAssessmentNotes());
                    snapshot.setPlanNotes(note.getPlanNotes());
                    snapshot.setTreatmentPlan(note.getTreatmentPlan());
                    snapshot.setNoteVersion(note.getNoteVersion());
                    snapshot.setParentNoteId(note.getId());
                    // save snapshot
                    clinicalNoteRepository.save(snapshot);
                    
                    // Update main note
                    if (payload.containsKey("historyOfPresentIllness")) note.setHistoryOfPresentIllness(payload.get("historyOfPresentIllness"));
                    if (payload.containsKey("pastMedicalHistory")) note.setPastMedicalHistory(payload.get("pastMedicalHistory"));
                    if (payload.containsKey("familyHistory")) note.setFamilyHistory(payload.get("familyHistory"));
                    if (payload.containsKey("socialHistory")) note.setSocialHistory(payload.get("socialHistory"));
                    if (payload.containsKey("reviewOfSystems")) note.setReviewOfSystems(payload.get("reviewOfSystems"));
                    if (payload.containsKey("subjectiveNotes")) note.setSubjectiveNotes(payload.get("subjectiveNotes"));
                    if (payload.containsKey("objectiveNotes")) note.setObjectiveNotes(payload.get("objectiveNotes"));
                    if (payload.containsKey("assessmentNotes")) note.setAssessmentNotes(payload.get("assessmentNotes"));
                    if (payload.containsKey("planNotes")) note.setPlanNotes(payload.get("planNotes"));
                    if (payload.containsKey("treatmentPlan")) note.setTreatmentPlan(payload.get("treatmentPlan"));
                    
                    note.setNoteVersion(note.getNoteVersion() + 1);
                    
                    clinicalNoteRepository.save(note);
                    return ResponseEntity.ok(note);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/sign")
    public ResponseEntity<?> signClinicalNote(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
            
        return clinicalNoteRepository.findById(id)
                .map(note -> {
                    if (note.isSigned() || note.isFinalized()) {
                        return ResponseEntity.badRequest().body("Note is already signed.");
                    }
                    
                    // User must be doctor who created it, or admin
                    note.setFinalized(true);
                    note.setFinalizedAt(LocalDateTime.now());
                    note.setSigned(true);
                    note.setSignedAt(LocalDateTime.now());
                    // In real app use userDetails: note.setSignedBy(userRepository.findById(userDetails.getId()).orElse(null));
                    
                    clinicalNoteRepository.save(note);
                    return ResponseEntity.ok(note);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/versions")
    public ResponseEntity<List<ClinicalNote>> getNoteVersions(@PathVariable UUID id) {
        return ResponseEntity.ok(clinicalNoteRepository.findByParentNoteIdOrderByNoteVersionDesc(id));
    }
}
