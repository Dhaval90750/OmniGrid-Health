package com.medcore.his.controller;

import com.medcore.his.domain.patient.PatientConsent;
import com.medcore.his.service.ConsentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/consent")
public class ConsentController {

    private final ConsentService consentService;

    @Autowired
    public ConsentController(ConsentService consentService) {
        this.consentService = consentService;
    }

    @PostMapping("/patient/{patientId}")
    public ResponseEntity<PatientConsent> recordConsent(@PathVariable UUID patientId, @RequestBody PatientConsent consent) {
        return ResponseEntity.ok(consentService.recordConsent(patientId, consent));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PatientConsent>> getPatientConsents(@PathVariable UUID patientId) {
        return ResponseEntity.ok(consentService.getPatientConsents(patientId));
    }

    @PutMapping("/{consentId}/revoke")
    public ResponseEntity<PatientConsent> revokeConsent(@PathVariable UUID consentId) {
        return ResponseEntity.ok(consentService.revokeConsent(consentId));
    }
}
