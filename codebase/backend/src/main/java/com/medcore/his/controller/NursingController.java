package com.medcore.his.controller;

import com.medcore.his.domain.nursing.MedAdministration;
import com.medcore.his.domain.nursing.PatientVital;
import com.medcore.his.service.NursingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_NURSE', 'ROLE_DOCTOR')")
@RestController
@RequestMapping("/api/v1/nursing")
public class NursingController {

    private final NursingService nursingService;

    @Autowired
    public NursingController(NursingService nursingService) {
        this.nursingService = nursingService;
    }

    @GetMapping("/vitals/patient/{patientId}")
    public ResponseEntity<List<PatientVital>> getVitals(@PathVariable UUID patientId) {
        return ResponseEntity.ok(nursingService.getVitalsByPatient(patientId));
    }

    @PostMapping("/vitals")
    public ResponseEntity<PatientVital> recordVitals(@RequestBody PatientVital vital) {
        return new ResponseEntity<>(nursingService.saveVitals(vital), HttpStatus.CREATED);
    }

    @GetMapping("/mar/patient/{patientId}")
    public ResponseEntity<List<MedAdministration>> getMar(@PathVariable UUID patientId) {
        return ResponseEntity.ok(nursingService.getMarForPatient(patientId));
    }

    @PostMapping("/mar")
    public ResponseEntity<MedAdministration> recordAdministration(@RequestBody MedAdministration admin) {
        return new ResponseEntity<>(nursingService.recordAdministration(admin), HttpStatus.CREATED);
    }

    @GetMapping("/handovers/patient/{patientId}")
    public ResponseEntity<List<com.medcore.his.domain.nursing.ShiftHandover>> getHandovers(@PathVariable UUID patientId) {
        return ResponseEntity.ok(nursingService.getHandoversForPatient(patientId));
    }

    @PostMapping("/handovers")
    public ResponseEntity<com.medcore.his.domain.nursing.ShiftHandover> saveHandover(@RequestBody com.medcore.his.domain.nursing.ShiftHandover handover) {
        return new ResponseEntity<>(nursingService.saveShiftHandover(handover), HttpStatus.CREATED);
    }

    @GetMapping("/assessments/patient/{patientId}")
    public ResponseEntity<List<com.medcore.his.domain.nursing.NursingAssessment>> getAssessments(@PathVariable UUID patientId) {
        return ResponseEntity.ok(nursingService.getAssessmentsForPatient(patientId));
    }

    @PostMapping("/assessments")
    public ResponseEntity<com.medcore.his.domain.nursing.NursingAssessment> saveAssessment(@RequestBody com.medcore.his.domain.nursing.NursingAssessment assessment) {
        return new ResponseEntity<>(nursingService.saveAssessment(assessment), HttpStatus.CREATED);
    }
}
