package com.medcore.his.controller;

import com.medcore.his.domain.lab.LabOrder;
import com.medcore.his.domain.lab.LabResult;
import com.medcore.his.domain.lab.LabSample;
import com.medcore.his.domain.lab.LabTest;
import com.medcore.his.service.LabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/lab")
public class LabController {

    private final LabService labService;

    @Autowired
    public LabController(LabService labService) {
        this.labService = labService;
    }

    @GetMapping("/tests/search")
    public ResponseEntity<List<LabTest>> searchTests(@RequestParam String query) {
        return ResponseEntity.ok(labService.searchTests(query));
    }

    @PostMapping("/orders")
    public ResponseEntity<LabOrder> createOrder(@RequestBody LabOrder order) {
        LabOrder saved = labService.placeOrder(order);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/orders/{orderId}/generate-barcodes")
    public ResponseEntity<List<LabSample>> generateBarcodes(@PathVariable UUID orderId) {
        return ResponseEntity.ok(labService.generateSampleBarcodes(orderId));
    }

    @PostMapping("/samples/{sampleId}/collect")
    public ResponseEntity<LabSample> collectSample(@PathVariable UUID sampleId) {
        // In reality, inject currently authenticated user
        return ResponseEntity.ok(labService.collectSample(sampleId, null));
    }

    @PostMapping("/samples/{sampleId}/authorize")
    public ResponseEntity<Void> authorizeResults(@PathVariable UUID sampleId, @RequestBody List<LabResult> results) {
        labService.authorizeResults(sampleId, results, null);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/samples/{sampleId}/receive")
    public ResponseEntity<LabSample> receiveSample(@PathVariable UUID sampleId, @RequestBody java.util.Map<String, Object> payload) {
        boolean accept = (Boolean) payload.getOrDefault("accept", true);
        String reason = (String) payload.get("reason");
        return ResponseEntity.ok(labService.receiveSample(sampleId, accept, reason));
    }

    @PostMapping("/samples/{sampleId}/results")
    public ResponseEntity<List<LabResult>> enterResults(@PathVariable UUID sampleId, @RequestBody List<LabResult> results) {
        return ResponseEntity.ok(labService.enterResults(sampleId, results, null));
    }
}
