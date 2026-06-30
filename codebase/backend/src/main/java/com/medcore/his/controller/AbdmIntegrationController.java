package com.medcore.his.controller;

import com.medcore.his.service.AbdmIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/abdm")
public class AbdmIntegrationController {

    private final AbdmIntegrationService abdmIntegrationService;

    @Autowired
    public AbdmIntegrationController(AbdmIntegrationService abdmIntegrationService) {
        this.abdmIntegrationService = abdmIntegrationService;
    }

    @PostMapping("/link/initiate")
    public ResponseEntity<Map<String, Object>> initiateAbhaLink(@RequestBody Map<String, String> request) {
        String abhaAddress = request.get("abhaAddress");
        if (abhaAddress == null || abhaAddress.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "abhaAddress is required"));
        }
        return ResponseEntity.ok(abdmIntegrationService.initiateAbhaLink(abhaAddress));
    }

    @PostMapping("/link/verify")
    public ResponseEntity<Map<String, Object>> verifyAbhaLink(@RequestBody Map<String, String> request) {
        String abhaAddress = request.get("abhaAddress");
        String otp = request.get("otp");
        String patientIdStr = request.get("patientId");

        if (abhaAddress == null || otp == null || patientIdStr == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "abhaAddress, otp, and patientId are required"));
        }

        UUID patientId;
        try {
            patientId = UUID.fromString(patientIdStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid patientId format"));
        }

        return ResponseEntity.ok(abdmIntegrationService.verifyAbhaOtp(abhaAddress, otp, patientId));
    }
}
