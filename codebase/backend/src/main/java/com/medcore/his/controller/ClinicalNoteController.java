package com.medcore.his.controller;

import com.medcore.his.domain.clinical.ClinicalNote;
import com.medcore.his.repository.ClinicalNoteRepository;
import com.medcore.his.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
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
                    if (note.isFinalized()) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot edit a finalized/signed note.");
                    }
                    
                    if (payload.containsKey("historyOfPresentIllness")) note.setHistoryOfPresentIllness(payload.get("historyOfPresentIllness"));
                    if (payload.containsKey("pastMedicalHistory")) note.setPastMedicalHistory(payload.get("pastMedicalHistory"));
                    if (payload.containsKey("physicalExamination")) note.setPhysicalExamination(payload.get("physicalExamination"));
                    if (payload.containsKey("treatmentPlan")) note.setTreatmentPlan(payload.get("treatmentPlan"));
                    
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
                    if (note.isFinalized()) {
                        return ResponseEntity.badRequest().body("Note is already signed.");
                    }
                    
                    // In a real app, verify userDetails.getId() == note.getDoctor().getId()
                    note.setFinalized(true);
                    note.setFinalizedAt(LocalDateTime.now());
                    
                    clinicalNoteRepository.save(note);
                    return ResponseEntity.ok(note);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
