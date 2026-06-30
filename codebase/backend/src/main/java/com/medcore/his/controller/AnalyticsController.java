package com.medcore.his.controller;

import com.medcore.his.service.AnalyticsService;
import com.medcore.his.service.PredictiveAnalyticsService;
import com.medcore.his.service.ClinicalNlpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final PredictiveAnalyticsService predictiveAnalyticsService;
    private final ClinicalNlpService clinicalNlpService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService, PredictiveAnalyticsService predictiveAnalyticsService, ClinicalNlpService clinicalNlpService) {
        this.analyticsService = analyticsService;
        this.predictiveAnalyticsService = predictiveAnalyticsService;
        this.clinicalNlpService = clinicalNlpService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        Map<String, Object> response = new HashMap<>();

        response.put("executive_overview", analyticsService.getExecutiveOverview());
        response.put("ai_risk_alerts", analyticsService.getAiRiskAlerts());
        response.put("quality_kpis", analyticsService.getQualityKpis());
        response.put("revenue_by_department", analyticsService.getRevenueByDepartment());

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to calculate Sepsis Risk based on vitals
     */
    @PostMapping("/sepsis-risk")
    public ResponseEntity<Map<String, Object>> getSepsisRisk(@RequestBody Map<String, Object> payload) {
        double temperature = Double.parseDouble(payload.getOrDefault("temperature", "37.0").toString());
        int heartRate = Integer.parseInt(payload.getOrDefault("heartRate", "80").toString());
        int respiratoryRate = Integer.parseInt(payload.getOrDefault("respiratoryRate", "16").toString());
        
        Map<String, Object> risk = predictiveAnalyticsService.calculateSepsisRisk(temperature, heartRate, respiratoryRate);
        return ResponseEntity.ok(risk);
    }

    /**
     * Endpoint to calculate 30-day Readmission Risk based on LACE-like parameters
     */
    @PostMapping("/readmission-risk")
    public ResponseEntity<Map<String, Object>> getReadmissionRisk(@RequestBody Map<String, Object> payload) {
        int lengthOfStay = Integer.parseInt(payload.getOrDefault("lengthOfStayDays", "1").toString());
        boolean isEmergency = Boolean.parseBoolean(payload.getOrDefault("isEmergencyAdmission", "false").toString());
        int comorbidities = Integer.parseInt(payload.getOrDefault("numComorbidities", "0").toString());
        int erVisits = Integer.parseInt(payload.getOrDefault("erVisitsPast6Months", "0").toString());
        
        Map<String, Object> risk = predictiveAnalyticsService.calculateReadmissionRisk(lengthOfStay, isEmergency, comorbidities, erVisits);
        return ResponseEntity.ok(risk);
    }

    /**
     * Endpoint to perform Intelligent ICD-10 Search via LLM
     */
    @PostMapping("/icd10-suggest")
    public ResponseEntity<java.util.List<Map<String, String>>> suggestIcd10(@RequestBody Map<String, String> payload) {
        String diagnosis = payload.get("diagnosis");
        if (diagnosis == null || diagnosis.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        java.util.List<Map<String, String>> codes = clinicalNlpService.suggestIcd10Codes(diagnosis);
        return ResponseEntity.ok(codes);
    }
}
