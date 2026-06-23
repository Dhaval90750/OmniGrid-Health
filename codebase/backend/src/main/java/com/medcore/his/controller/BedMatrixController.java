package com.medcore.his.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/admin/bed-matrix")
public class BedMatrixController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getBedMatrix() {
        // Mock data for the 2D Bed Matrix across various wards
        List<Map<String, Object>> wards = Arrays.asList(
            Map.of(
                "wardName", "Intensive Care Unit (ICU)",
                "totalBeds", 10,
                "occupied", 8,
                "beds", Arrays.asList(
                    Map.of("bedId", "ICU-01", "status", "Occupied", "patient", "David Chen"),
                    Map.of("bedId", "ICU-02", "status", "Occupied", "patient", "Sarah Miller"),
                    Map.of("bedId", "ICU-03", "status", "Available", "patient", ""),
                    Map.of("bedId", "ICU-04", "status", "Maintenance", "patient", "")
                )
            ),
            Map.of(
                "wardName", "General Ward A",
                "totalBeds", 20,
                "occupied", 15,
                "beds", Arrays.asList(
                    Map.of("bedId", "WA-01", "status", "Occupied", "patient", "John Doe"),
                    Map.of("bedId", "WA-02", "status", "Cleaning", "patient", ""),
                    Map.of("bedId", "WA-03", "status", "Available", "patient", "")
                )
            )
        );

        return ResponseEntity.ok(Map.of("wards", wards));
    }
}
