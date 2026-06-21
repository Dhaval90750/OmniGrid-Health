package com.medcore.his.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        Map<String, Object> response = new HashMap<>();

        // MOCK: Executive Overview
        Map<String, Object> executive = new HashMap<>();
        executive.put("total_revenue_today", 154000.00); // INR or USD
        executive.put("active_ipd_census", 142);
        executive.put("bed_occupancy_percent", 84.5);
        executive.put("er_waiting_time_mins", 14);
        response.put("executive_overview", executive);

        // MOCK: AI Risk Predictors
        List<Map<String, Object>> aiAlerts = Arrays.asList(
            Map.of("patient_id", "MED-1001", "name", "Rajesh Kumar", "ward", "ICU", "alert_type", "Sepsis Risk", "probability", 0.89, "status", "CRITICAL"),
            Map.of("patient_id", "MED-1045", "name", "Sunita Verma", "ward", "Ward B", "alert_type", "30-Day Readmission", "probability", 0.76, "status", "HIGH"),
            Map.of("patient_id", "MED-1102", "name", "John Doe", "ward", "Post-Op", "alert_type", "ICU Transfer Risk", "probability", 0.65, "status", "MODERATE")
        );
        response.put("ai_risk_alerts", aiAlerts);

        // MOCK: Quality & Compliance (NABH KPIs)
        Map<String, Object> qualityKpis = new HashMap<>();
        qualityKpis.put("average_length_of_stay", 4.2); // Days
        qualityKpis.put("hospital_acquired_infections", 1.2); // Percent
        qualityKpis.put("surgical_site_infections", 0.8); // Percent
        qualityKpis.put("patient_satisfaction_score", 4.6); // Out of 5.0
        response.put("quality_kpis", qualityKpis);

        // MOCK: Revenue by Department
        List<Map<String, Object>> revenueSplit = Arrays.asList(
            Map.of("department", "Cardiology", "revenue", 45000.00),
            Map.of("department", "Orthopedics", "revenue", 38000.00),
            Map.of("department", "Neurology", "revenue", 29000.00),
            Map.of("department", "General Medicine", "revenue", 22000.00),
            Map.of("department", "ER & Trauma", "revenue", 20000.00)
        );
        response.put("revenue_by_department", revenueSplit);

        return ResponseEntity.ok(response);
    }
}
