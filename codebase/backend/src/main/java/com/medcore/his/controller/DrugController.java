package com.medcore.his.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/drugs")
public class DrugController {

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, String>>> searchDrugs(@RequestParam String q) {
        // Mocked Drug Catalog for MVP
        List<Map<String, String>> mockCatalog = List.of(
            Map.of("genericName", "Paracetamol", "brandName", "Tylenol", "strength", "500mg", "form", "Tablet"),
            Map.of("genericName", "Amoxicillin", "brandName", "Amoxil", "strength", "250mg", "form", "Capsule"),
            Map.of("genericName", "Ibuprofen", "brandName", "Advil", "strength", "400mg", "form", "Tablet"),
            Map.of("genericName", "Metformin", "brandName", "Glucophage", "strength", "500mg", "form", "Tablet"),
            Map.of("genericName", "Lisinopril", "brandName", "Prinivil", "strength", "10mg", "form", "Tablet"),
            Map.of("genericName", "Atorvastatin", "brandName", "Lipitor", "strength", "20mg", "form", "Tablet")
        );
        
        List<Map<String, String>> filtered = mockCatalog.stream()
            .filter(d -> d.get("genericName").toLowerCase().contains(q.toLowerCase()) || d.get("brandName").toLowerCase().contains(q.toLowerCase()))
            .toList();
            
        return ResponseEntity.ok(filtered);
    }
}
