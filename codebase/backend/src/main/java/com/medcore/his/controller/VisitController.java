package com.medcore.his.controller;

import com.medcore.his.domain.clinical.Visit;
import com.medcore.his.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/visits")
public class VisitController {

    private final VisitRepository visitRepository;

    @Autowired
    public VisitController(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Visit>> getVisitsForDoctor(@PathVariable UUID doctorId) {
        // In a real scenario, doctorId would come from the JWT Context
        return ResponseEntity.ok(visitRepository.findByDoctorIdOrderByVisitDateDesc(doctorId));
    }
}
