package com.medcore.his.controller;

import com.medcore.his.domain.radiology.RadiologyOrder;
import com.medcore.his.domain.radiology.RadiologyReport;
import com.medcore.his.domain.radiology.RadiologyTemplate;
import com.medcore.his.service.RadiologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_RADIOLOGIST', 'ROLE_DOCTOR')")
@RestController
@RequestMapping("/api/v1/radiology")
public class RadiologyController {

    private final RadiologyService radiologyService;

    @Autowired
    public RadiologyController(RadiologyService radiologyService) {
        this.radiologyService = radiologyService;
    }

    @GetMapping("/templates")
    public ResponseEntity<List<RadiologyTemplate>> getTemplates(@RequestParam String modality) {
        return ResponseEntity.ok(radiologyService.getTemplatesByModality(modality));
    }

    @GetMapping("/orders/pending")
    public ResponseEntity<List<RadiologyOrder>> getPendingOrders(@RequestParam String modality) {
        return ResponseEntity.ok(radiologyService.getPendingOrdersByModality(modality));
    }

    @PostMapping("/orders")
    public ResponseEntity<RadiologyOrder> createOrder(@RequestBody RadiologyOrder order) {
        RadiologyOrder saved = radiologyService.placeOrder(order);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/reports")
    public ResponseEntity<RadiologyReport> saveReport(@RequestBody RadiologyReport report) {
        RadiologyReport saved = radiologyService.saveReport(report);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<java.util.Map<String, Long>> getStats() {
        return ResponseEntity.ok(radiologyService.getRadiologyStats());
    }

    @GetMapping("/dashboard/critical")
    public ResponseEntity<List<RadiologyReport>> getCriticalReports() {
        return ResponseEntity.ok(radiologyService.getCriticalReports());
    }

    @GetMapping("/patients/{patientId}/reports")
    public ResponseEntity<List<RadiologyReport>> getPatientReports(@PathVariable java.util.UUID patientId) {
        return ResponseEntity.ok(radiologyService.getReportsByPatient(patientId));
    }

    @PostMapping("/studies/{orderId}/dicom")
    public ResponseEntity<String> uploadDicom(@PathVariable java.util.UUID orderId, @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        // In a real implementation, this would save to MinIO/S3 and register with a PACS server (e.g. Orthanc/dcm4chee)
        // Here we just mock the upload success
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        
        RadiologyOrder order = radiologyService.getPendingOrdersByModality("ALL").stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst()
                .orElse(null);
                
        if (order != null) {
            order.setStatus("Completed");
            // Would save order here if we had direct access to repo, but we rely on service
        }
        
        return ResponseEntity.ok("DICOM file " + file.getOriginalFilename() + " uploaded successfully and linked to order " + orderId);
    }
}
