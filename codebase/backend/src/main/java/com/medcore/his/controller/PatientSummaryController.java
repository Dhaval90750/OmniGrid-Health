package com.medcore.his.controller;

import com.medcore.his.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/patients")
public class PatientSummaryController {

    private final PatientRepository patientRepository;
    private final PatientVitalRepository patientVitalRepository;
    private final AllergyRepository allergyRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final ClinicalNoteRepository clinicalNoteRepository;

    @Autowired
    public PatientSummaryController(
            PatientRepository patientRepository,
            PatientVitalRepository patientVitalRepository,
            AllergyRepository allergyRepository,
            DiagnosisRepository diagnosisRepository,
            ClinicalNoteRepository clinicalNoteRepository) {
        this.patientRepository = patientRepository;
        this.patientVitalRepository = patientVitalRepository;
        this.allergyRepository = allergyRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.clinicalNoteRepository = clinicalNoteRepository;
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<?> getPatientSummary(@PathVariable UUID id) {
        return patientRepository.findById(id)
                .map(patient -> {
                    Map<String, Object> summary = new HashMap<>();
                    summary.put("patient", patient);
                    
                    // Fetch recent vitals (top 5)
                    var vitals = patientVitalRepository.findByPatientIdOrderByRecordedAtDesc(id);
                    summary.put("vitals", vitals.size() > 5 ? vitals.subList(0, 5) : vitals);
                    
                    // Fetch active allergies
                    var allergies = allergyRepository.findByPatientIdAndStatus(id, "Active");
                    summary.put("allergies", allergies);
                    
                    // Fetch active diagnoses
                    var diagnoses = diagnosisRepository.findByPatientIdAndStatusOrderByDiagnosedDateDesc(id, "Active");
                    summary.put("activeDiagnoses", diagnoses);
                    
                    // Fetch recent clinical notes
                    var notes = clinicalNoteRepository.findByVisitPatientIdOrderByCreatedAtDesc(id);
                    summary.put("recentNotes", notes.size() > 5 ? notes.subList(0, 5) : notes);
                    
                    return ResponseEntity.ok(summary);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
