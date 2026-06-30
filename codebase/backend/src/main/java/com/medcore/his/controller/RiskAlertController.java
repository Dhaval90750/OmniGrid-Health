package com.medcore.his.controller;

import com.medcore.his.service.RiskAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/risk")
public class RiskAlertController {

    private final RiskAlertService riskAlertService;

    @Autowired
    public RiskAlertController(RiskAlertService riskAlertService) {
        this.riskAlertService = riskAlertService;
    }

    @GetMapping("/{patientId}/sepsis")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> checkSepsisRisk(@PathVariable UUID patientId) {
        Map<String, Object> riskResult = riskAlertService.evaluateSepsisRisk(patientId);
        return ResponseEntity.ok(riskResult);
    }

    @GetMapping("/{patientId}/readmission")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> checkReadmissionRisk(
            @PathVariable UUID patientId,
            @RequestParam(defaultValue = "1") int lengthOfStayDays,
            @RequestParam(defaultValue = "false") boolean isEmergency) {
        
        Map<String, Object> riskResult = riskAlertService.evaluateReadmissionRisk(patientId, lengthOfStayDays, isEmergency);
        return ResponseEntity.ok(riskResult);
    }

    @GetMapping("/{patientId}/summary")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCompleteRiskProfile(@PathVariable UUID patientId) {
        Map<String, Object> sepsis = riskAlertService.evaluateSepsisRisk(patientId);
        // Default to a 3-day non-emergency readmission check if not specified
        Map<String, Object> readmission = riskAlertService.evaluateReadmissionRisk(patientId, 3, false);
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("sepsis", sepsis);
        profile.put("readmission", readmission);
        
        return ResponseEntity.ok(profile);
    }
}
