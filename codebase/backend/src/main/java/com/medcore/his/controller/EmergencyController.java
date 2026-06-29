package com.medcore.his.controller;

import com.medcore.his.domain.clinical.TriageAssessment;
import com.medcore.his.service.EmergencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/emergency")
public class EmergencyController {

    private final EmergencyService emergencyService;

    @Autowired
    public EmergencyController(EmergencyService emergencyService) {
        this.emergencyService = emergencyService;
    }

    @PostMapping("/triage/{patientId}")
    public ResponseEntity<TriageAssessment> recordTriage(@PathVariable UUID patientId, @RequestBody TriageAssessment assessment) {
        return ResponseEntity.ok(emergencyService.recordTriage(patientId, assessment));
    }

    @GetMapping("/active")
    public ResponseEntity<List<TriageAssessment>> getActiveEmergencies() {
        return ResponseEntity.ok(emergencyService.getActiveEmergencies());
    }

    @PutMapping("/triage/{assessmentId}/status")
    public ResponseEntity<TriageAssessment> updateStatus(@PathVariable UUID assessmentId, @RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(emergencyService.updateStatus(assessmentId, payload.get("status")));
    }
}
