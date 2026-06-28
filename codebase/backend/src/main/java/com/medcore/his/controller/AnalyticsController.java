package com.medcore.his.controller;

import com.medcore.his.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
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
}
