package com.medcore.his.controller;

import com.medcore.his.domain.clinical.TriageAssessment;
import com.medcore.his.service.EmergencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_DOCTOR', 'ROLE_NURSE', 'ROLE_RECEPTIONIST')")
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

    @PostMapping("/break-glass")
    public ResponseEntity<Void> breakGlass(@RequestBody Map<String, String> payload, @RequestHeader(value = "X-Forwarded-For", defaultValue = "unknown") String ipAddress) {
        UUID patientId = UUID.fromString(payload.get("patientId"));
        String reason = payload.get("reason");
        // In a real app, grab userId from SecurityContext
        UUID userId = UUID.randomUUID(); // Mocking authenticated user
        
        emergencyService.logBreakGlass(patientId, userId, reason, ipAddress);
        return ResponseEntity.ok().build();
    }
}
