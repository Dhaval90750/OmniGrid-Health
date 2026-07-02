package com.medcore.his.controller;

import com.medcore.his.domain.clinical.Admission;
import com.medcore.his.domain.master.Bed;
import com.medcore.his.service.AdmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_RECEPTIONIST', 'ROLE_DOCTOR')")
@RestController
@RequestMapping("/api/v1/admissions")
public class AdmissionController {

    private final AdmissionService admissionService;

    @Autowired
    public AdmissionController(AdmissionService admissionService) {
        this.admissionService = admissionService;
    }

    @PostMapping
    public ResponseEntity<Admission> admitPatient(@RequestBody Admission admission) {
        Admission savedAdmission = admissionService.admitPatient(admission);
        return new ResponseEntity<>(savedAdmission, HttpStatus.CREATED);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Admission>> getActiveAdmissions() {
        return ResponseEntity.ok(admissionService.getActiveAdmissions());
    }

    @PutMapping("/{id}/discharge")
    public ResponseEntity<Admission> dischargePatient(
            @PathVariable java.util.UUID id, 
            @RequestBody java.util.Map<String, String> payload) {
        String dischargeSummary = payload.get("dischargeSummary");
        Admission discharged = admissionService.dischargePatient(id, dischargeSummary);
        return ResponseEntity.ok(discharged);
    }

    @PutMapping("/beds/{bedId}/clean")
    public ResponseEntity<Bed> markBedClean(@PathVariable java.util.UUID bedId) {
        return ResponseEntity.ok(admissionService.markBedClean(bedId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Admission> getAdmissionById(@PathVariable java.util.UUID id) {
        Admission admission = admissionService.getAdmission(id);
        if (admission == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(admission);
    }

    @PutMapping("/{id}/transfer")
    public ResponseEntity<Admission> transferPatient(
            @PathVariable java.util.UUID id, 
            @RequestBody java.util.Map<String, String> payload) {
        java.util.UUID newBedId = java.util.UUID.fromString(payload.get("newBedId"));
        Admission transferred = admissionService.transferPatient(id, newBedId);
        return ResponseEntity.ok(transferred);
    }
}
