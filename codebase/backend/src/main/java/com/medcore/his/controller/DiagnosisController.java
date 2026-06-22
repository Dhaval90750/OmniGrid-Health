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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
public class DiagnosisController {

    private final DiagnosisRepository diagnosisRepository;
    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;

    @Autowired
    public DiagnosisController(DiagnosisRepository diagnosisRepository, PatientRepository patientRepository, VisitRepository visitRepository) {
        this.diagnosisRepository = diagnosisRepository;
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
    }

    // --- Mock ICD-10 Search ---
    @GetMapping("/icd/search")
    public ResponseEntity<List<Map<String, String>>> searchIcd10(@RequestParam String q) {
        // Mock dataset for MVP
        List<Map<String, String>> mockData = List.of(
            Map.of("code", "E11.9", "description", "Type 2 diabetes mellitus without complications"),
            Map.of("code", "I10", "description", "Essential (primary) hypertension"),
            Map.of("code", "J45.909", "description", "Unspecified asthma, uncomplicated"),
            Map.of("code", "R07.9", "description", "Chest pain, unspecified"),
            Map.of("code", "E78.5", "description", "Hyperlipidemia, unspecified")
        );
        
        List<Map<String, String>> filtered = mockData.stream()
            .filter(d -> d.get("description").toLowerCase().contains(q.toLowerCase()) || d.get("code").toLowerCase().contains(q.toLowerCase()))
            .toList();
            
        return ResponseEntity.ok(filtered);
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
