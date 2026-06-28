package com.medcore.his.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateReport(@RequestBody Map<String, Object> requestParams) {
        String dataset = (String) requestParams.getOrDefault("dataset", "invoices");
        
        Map<String, Object> response = new HashMap<>();
        response.put("dataset", dataset);
        response.put("generatedAt", System.currentTimeMillis());
        
        List<Map<String, Object>> rows = new ArrayList<>();
        
        // Prototype logic: In a real implementation, this would dynamically parse the requested dataset
        // and apply Specification/Criteria API to filter results before returning the dataset.
        if ("invoices".equalsIgnoreCase(dataset)) {
            rows.add(Map.of("invoiceId", "INV-1001", "patientName", "Rajesh Kumar", "amount", 1200.00, "status", "PAID"));
            rows.add(Map.of("invoiceId", "INV-1002", "patientName", "Sunita Verma", "amount", 4500.00, "status", "PENDING"));
        } else if ("admissions".equalsIgnoreCase(dataset)) {
            rows.add(Map.of("admissionId", "ADM-901", "patientName", "John Doe", "ward", "ICU", "status", "ADMITTED"));
        }
        
        response.put("data", rows);
        response.put("totalRows", rows.size());
        
        return ResponseEntity.ok(response);
    }
}
