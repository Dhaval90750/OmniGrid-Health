package com.medcore.his.controller;

import com.medcore.his.service.PortalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/portal")
public class PortalController {

    private final PortalService portalService;

    @Autowired
    public PortalController(PortalService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/dashboard/{patientId}")
    public ResponseEntity<Map<String, Object>> getDashboard(@PathVariable UUID patientId) {
        return ResponseEntity.ok(portalService.getPatientDashboard(patientId));
    }
    
    // In a real scenario, this would have endpoints for downloading PDFs of reports,
    // initiating WebRTC signaling for Telemedicine, etc.
}
