package com.medcore.his.service;

import com.medcore.his.domain.clinical.Visit;
import com.medcore.his.domain.patient.Patient;
import com.medcore.his.repository.PatientRepository;
import com.medcore.his.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PortalService {

    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;
    private final com.medcore.his.repository.LabResultRepository labResultRepository;
    private final com.medcore.his.repository.RadiologyReportRepository radiologyReportRepository;

    @Autowired
    public PortalService(PatientRepository patientRepository, VisitRepository visitRepository, 
                         com.medcore.his.repository.LabResultRepository labResultRepository,
                         com.medcore.his.repository.RadiologyReportRepository radiologyReportRepository) {
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
        this.labResultRepository = labResultRepository;
        this.radiologyReportRepository = radiologyReportRepository;
    }

    public Map<String, Object> getPatientDashboard(UUID patientId) {
        Map<String, Object> dashboard = new HashMap<>();
        
        Patient patient = patientRepository.findById(patientId).orElse(null);
        if (patient != null) {
            dashboard.put("patientName", patient.getFirstName() + " " + patient.getLastName());
            dashboard.put("uhid", patient.getUhid());
        }
        
        List<Visit> visits = visitRepository.findByPatientIdOrderByVisitDateDesc(patientId);
        
        dashboard.put("upcomingAppointments", visits.stream()
                .filter(v -> v.getVisitDate() != null && v.getVisitDate().isAfter(java.time.LocalDateTime.now()))
                .limit(5).toList());
        
        List<Map<String, Object>> recentReports = new java.util.ArrayList<>();
        
        labResultRepository.findAll().stream()
                .filter(r -> r.getSample().getOrder().getPatient().getId().equals(patientId))
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .limit(3)
                .forEach(r -> {
                    recentReports.add(Map.of(
                        "id", "LAB-" + r.getId().toString().substring(0, 8),
                        "type", r.getSample().getTest().getTestName(),
                        "date", r.getCreatedAt().toString(),
                        "status", "Final"
                    ));
                });
                
        radiologyReportRepository.findAll().stream()
                .filter(r -> r.getOrder().getPatient().getId().equals(patientId))
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .limit(3)
                .forEach(r -> {
                    recentReports.add(Map.of(
                        "id", "RAD-" + r.getId().toString().substring(0, 8),
                        "type", r.getOrder().getModality() + " - " + r.getOrder().getStudyDescription(),
                        "date", r.getCreatedAt().toString(),
                        "status", r.getStatus()
                    ));
                });
                
        dashboard.put("recentReports", recentReports);
        
        return dashboard;
    }
}
