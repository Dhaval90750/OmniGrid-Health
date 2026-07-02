package com.medcore.his.controller;

import com.medcore.his.domain.clinical.Diagnosis;
import com.medcore.his.domain.patient.Patient;
import com.medcore.his.domain.clinical.Visit;
import com.medcore.his.repository.DiagnosisRepository;
import com.medcore.his.repository.PatientRepository;
import com.medcore.his.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_DOCTOR')")
@RestController
@RequestMapping("/api/v1")
public class DiagnosisController {

    private final DiagnosisRepository diagnosisRepository;
    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;
    private final com.medcore.his.repository.Icd10Repository icd10Repository;

    @Autowired
    public DiagnosisController(DiagnosisRepository diagnosisRepository, PatientRepository patientRepository, VisitRepository visitRepository, com.medcore.his.repository.Icd10Repository icd10Repository) {
        this.diagnosisRepository = diagnosisRepository;
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
        this.icd10Repository = icd10Repository;
    }

    @GetMapping("/icd/search")
    public ResponseEntity<List<com.medcore.his.domain.clinical.Icd10>> searchIcd10(@RequestParam String q) {
        List<com.medcore.his.domain.clinical.Icd10> results = icd10Repository.searchByText(q);
        return ResponseEntity.ok(results);
    }

    // --- Patient Diagnoses ---
    @GetMapping("/patients/{patientId}/diagnoses")
    public ResponseEntity<List<Diagnosis>> getPatientDiagnoses(@PathVariable UUID patientId, @RequestParam(required = false) String status) {
        if (status != null) {
            return ResponseEntity.ok(diagnosisRepository.findByPatientIdAndStatusOrderByDiagnosedDateDesc(patientId, status));
        }
        return ResponseEntity.ok(diagnosisRepository.findByPatientIdOrderByDiagnosedDateDesc(patientId));
    }

    @PostMapping("/patients/{patientId}/diagnoses")
    public ResponseEntity<?> addDiagnosis(
            @PathVariable UUID patientId,
            @RequestBody Map<String, String> payload) {
        
        Patient patient = patientRepository.findById(patientId).orElse(null);
        if (patient == null) return ResponseEntity.badRequest().body("Patient not found");
        
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setPatient(patient);
        diagnosis.setDiagnosisName(payload.get("diagnosisName"));
        diagnosis.setIcd10Code(payload.get("icd10Code"));
        diagnosis.setType(payload.getOrDefault("type", "Provisional"));
        diagnosis.setStatus(payload.getOrDefault("status", "Active"));
        
        if (payload.containsKey("visitId") && payload.get("visitId") != null) {
            Visit visit = visitRepository.findById(UUID.fromString(payload.get("visitId"))).orElse(null);
            diagnosis.setVisit(visit);
        }
        
        return new ResponseEntity<>(diagnosisRepository.save(diagnosis), HttpStatus.CREATED);
    }

    @PutMapping("/patients/{patientId}/diagnoses/{diagnosisId}")
    public ResponseEntity<?> updateDiagnosis(
            @PathVariable UUID patientId,
            @PathVariable UUID diagnosisId,
            @RequestBody Map<String, String> payload) {
            
        return diagnosisRepository.findById(diagnosisId)
                .map(diagnosis -> {
                    if (payload.containsKey("status")) diagnosis.setStatus(payload.get("status"));
                    if (payload.containsKey("type")) diagnosis.setType(payload.get("type"));
                    return ResponseEntity.ok(diagnosisRepository.save(diagnosis));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
