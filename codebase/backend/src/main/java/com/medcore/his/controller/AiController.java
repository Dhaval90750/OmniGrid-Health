package com.medcore.his.controller;

import com.medcore.his.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiService aiService;

    @Autowired
    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/extract")
    public ResponseEntity<Map<String, Object>> extractEntities(@RequestBody Map<String, String> request) {
        String transcript = request.getOrDefault("transcript", "");
        return ResponseEntity.ok(aiService.extractMedicalEntities(transcript));
    }

    @PostMapping("/discharge-summary")
    public ResponseEntity<Map<String, Object>> generateDischargeSummary(@RequestBody Map<String, String> request) {
        String clinicalContext = request.getOrDefault("context", "");
        return ResponseEntity.ok(aiService.generateDischargeSummary(clinicalContext));
    }

    @PostMapping("/patient-oneliner")
    public ResponseEntity<Map<String, Object>> generatePatientOneLiner(@RequestBody Map<String, String> request) {
        String history = request.getOrDefault("history", "");
        return ResponseEntity.ok(aiService.generatePatientOneLiner(history));
    }

    @PostMapping("/voice-to-notes")
    public ResponseEntity<Map<String, Object>> processVoiceToNotes(@RequestBody Map<String, String> request) {
        String transcript = request.getOrDefault("transcript", "");
        return ResponseEntity.ok(aiService.extractMedicalEntities(transcript));
    }

    @PostMapping("/icd-suggest")
    public ResponseEntity<Map<String, Object>> suggestIcd(@RequestBody Map<String, String> request) {
        String text = request.getOrDefault("clinicalText", "");
        return ResponseEntity.ok(aiService.suggestIcdCodes(text));
    }

    @GetMapping("/predict/readmission")
    public ResponseEntity<Map<String, Object>> predictReadmission(
            @RequestParam int age,
            @RequestParam int los,
            @RequestParam boolean emergency,
            @RequestParam int comorbidities) {
        return ResponseEntity.ok(aiService.predictReadmissionRisk(age, los, emergency, comorbidities));
    }
}
