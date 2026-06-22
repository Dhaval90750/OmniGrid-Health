package com.medcore.his.controller;

import com.medcore.his.domain.clinical.Prescription;
import com.medcore.his.domain.clinical.PrescriptionLine;
import com.medcore.his.domain.clinical.Visit;
import com.medcore.his.repository.PrescriptionRepository;
import com.medcore.his.repository.VisitRepository;
import com.medcore.his.service.ClinicalRulesEngine;
import com.medcore.his.service.PdfGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
public class PrescriptionController {

    private final PrescriptionRepository prescriptionRepository;
    private final VisitRepository visitRepository;
    private final ClinicalRulesEngine rulesEngine;
    private final PdfGenerationService pdfService;

    @Autowired
    public PrescriptionController(PrescriptionRepository prescriptionRepository,
                                  VisitRepository visitRepository,
                                  ClinicalRulesEngine rulesEngine,
                                  PdfGenerationService pdfService) {
        this.prescriptionRepository = prescriptionRepository;
        this.visitRepository = visitRepository;
        this.rulesEngine = rulesEngine;
        this.pdfService = pdfService;
    }

    @PostMapping("/visits/{visitId}/prescriptions")
    public ResponseEntity<?> addPrescription(@PathVariable UUID visitId, @RequestBody Prescription prescription) {
        Visit visit = visitRepository.findById(visitId).orElse(null);
        if (visit == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Visit not found");
        }

        List<String> prescribedDrugs = prescription.getLines().stream()
                .map(PrescriptionLine::getCustomDrugName)
                .collect(Collectors.toList());

        List<String> alerts = rulesEngine.checkPrescriptionSafety(visit.getPatient().getId(), prescribedDrugs);
        if (!alerts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(alerts);
        }

        prescription.setVisit(visit);
        prescription.setPatient(visit.getPatient());
        prescription.setDoctor(visit.getDoctor());
        
        for (PrescriptionLine line : prescription.getLines()) {
            line.setPrescription(prescription);
        }

        return ResponseEntity.ok(prescriptionRepository.save(prescription));
    }

    @GetMapping("/patients/{patientId}/prescriptions")
    public ResponseEntity<List<Prescription>> getPatientPrescriptions(@PathVariable UUID patientId) {
        return ResponseEntity.ok(prescriptionRepository.findAll().stream()
                .filter(p -> p.getPatient().getId().equals(patientId))
                .collect(Collectors.toList()));
    }

    @GetMapping("/visits/{visitId}/prescription-pdf")
    public ResponseEntity<byte[]> generatePrescriptionPdf(@PathVariable UUID visitId) {
        Visit visit = visitRepository.findById(visitId).orElse(null);
        if (visit == null) {
            return ResponseEntity.notFound().build();
        }
        
        Prescription prescription = prescriptionRepository.findById(visitId).orElse(null); // Assuming findByVisitId in a real repo, here just an example or we can do a query.
        if (prescription == null) {
            // Wait, we need findByVisitId. Let's assume we fetch the first one if it's a list, or just use the ID if we map it directly.
            // I will use prescriptionRepository.findAll().stream().filter(p -> p.getVisit().getId().equals(visitId)).findFirst().orElse(null);
            prescription = prescriptionRepository.findAll().stream().filter(p -> p.getVisit().getId().equals(visitId)).findFirst().orElse(null);
        }
        if (prescription == null) return ResponseEntity.notFound().build();

        byte[] pdfBytes = pdfService.generatePrescriptionPdf(prescription);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "prescription_" + visitId + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
