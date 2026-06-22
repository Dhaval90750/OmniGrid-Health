package com.medcore.his.controller;

import com.medcore.his.domain.clinical.Visit;
import com.medcore.his.domain.clinical.ClinicalNote;
import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.patient.Patient;
import com.medcore.his.repository.VisitRepository;
import com.medcore.his.repository.ClinicalNoteRepository;
import com.medcore.his.repository.UserRepository;
import com.medcore.his.repository.PatientRepository;
import com.medcore.his.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/visits")
public class VisitController {

    private final VisitRepository visitRepository;
    private final ClinicalNoteRepository clinicalNoteRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public VisitController(VisitRepository visitRepository,
                           ClinicalNoteRepository clinicalNoteRepository,
                           UserRepository userRepository,
                           PatientRepository patientRepository) {
        this.visitRepository = visitRepository;
        this.clinicalNoteRepository = clinicalNoteRepository;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
    }

    @PostMapping
    public ResponseEntity<?> createVisit(
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        UUID patientId = UUID.fromString(payload.get("patientId"));
        UUID doctorId = UUID.fromString(payload.get("doctorId"));

        Patient patient = patientRepository.findById(patientId).orElse(null);
        User doctor = userRepository.findById(doctorId).orElse(null);

        if (patient == null || doctor == null) {
            return ResponseEntity.badRequest().body("Patient or Doctor not found");
        }

        Visit visit = new Visit();
        visit.setPatient(patient);
        visit.setDoctor(doctor);
        visit.setVisitType(payload.getOrDefault("visitType", "OPD"));
        visit.setStatus("SCHEDULED");
        visit.setVisitDate(LocalDateTime.now());
        visit.setChiefComplaint(payload.get("chiefComplaint"));

        // OPD Token Queue Allocation
        Integer maxToken = visitRepository.findMaxTokenNumberByDoctorIdAndVisitDate(doctorId, visit.getVisitDate());
        visit.setTokenNumber((maxToken == null ? 0 : maxToken) + 1);
        visit.setQueueStatus("WAITING");

        visitRepository.save(visit);

        return ResponseEntity.status(HttpStatus.CREATED).body(visit);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getVisitsForDoctor(
            @PathVariable UUID doctorId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        boolean hasAccess = userDetails.getId().equals(doctorId) || 
                userDetails.getAuthorities().stream().anyMatch(a -> 
                        a.getAuthority().equals("ROLE_ADMIN") || 
                        a.getAuthority().equals("ROLE_RECEPTIONIST")
                );

        if (!hasAccess) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. You cannot view other doctors' visits.");
        }

        return ResponseEntity.ok(visitRepository.findByDoctorIdOrderByVisitDateDesc(doctorId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVisitById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        return visitRepository.findById(id)
                .map(visit -> {
                    boolean hasAccess = visit.getDoctor() == null || visit.getDoctor().getId().equals(userDetails.getId()) || 
                            userDetails.getAuthorities().stream().anyMatch(a -> 
                                    a.getAuthority().equals("ROLE_ADMIN") || 
                                    a.getAuthority().equals("ROLE_RECEPTIONIST")
                            );
                    if (!hasAccess) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. You cannot view this visit.");
                    }
                    return ResponseEntity.ok(visit);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/notes")
    public ResponseEntity<?> createClinicalNote(
            @PathVariable UUID id,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        return visitRepository.findById(id)
                .map(visit -> {
                    boolean hasAccess = visit.getDoctor() == null || visit.getDoctor().getId().equals(userDetails.getId()) || 
                            userDetails.getAuthorities().stream().anyMatch(a -> 
                                    a.getAuthority().equals("ROLE_ADMIN")
                            );
                    if (!hasAccess) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
                    }

                    User doctor = userRepository.findById(userDetails.getId()).orElse(null);
                    if (doctor == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found.");
                    }

                    // Save Clinical Note
                    ClinicalNote note = new ClinicalNote();
                    note.setVisit(visit);
                    note.setDoctor(doctor);
                    note.setNoteType("OPD_CONSULT");
                    note.setHistoryOfPresentIllness(payload.get("historyOfPresentIllness"));
                    note.setPhysicalExamination(payload.get("physicalExamination"));
                    note.setTreatmentPlan(payload.get("treatmentPlan"));
                    note.setPastMedicalHistory(payload.get("pastMedicalHistory"));
                    note.setFinalized(true);
                    note.setFinalizedAt(LocalDateTime.now());

                    clinicalNoteRepository.save(note);

                    // Update Visit Status
                    visit.setStatus("COMPLETED");
                    visitRepository.save(visit);

                    return ResponseEntity.ok(Map.of("message", "Clinical note saved and visit completed successfully!"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateVisitStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        return visitRepository.findById(id)
                .map(visit -> {
                    String status = payload.get("status");
                    String queueStatus = payload.get("queueStatus");
                    
                    if (status != null) visit.setStatus(status);
                    if (queueStatus != null) visit.setQueueStatus(queueStatus);
                    
                    visitRepository.save(visit);
                    return ResponseEntity.ok(visit);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
