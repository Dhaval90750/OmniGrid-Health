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

    @Autowired
    public PortalService(PatientRepository patientRepository, VisitRepository visitRepository) {
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
    }

    public Map<String, Object> getPatientDashboard(UUID patientId) {
        Map<String, Object> dashboard = new HashMap<>();
        
        Patient patient = patientRepository.findById(patientId).orElse(null);
        if (patient != null) {
            dashboard.put("patientName", patient.getFirstName() + " " + patient.getLastName());
            dashboard.put("uhid", patient.getUhid());
        }
        
        List<Visit> visits = visitRepository.findByPatientIdOrderByVisitDateDesc(patientId);
        
        // Mock upcoming appointments (filter in real scenario)
        dashboard.put("upcomingAppointments", visits.stream().limit(2).toList());
        
        // Mock recent reports
        dashboard.put("recentReports", List.of(
            Map.of("id", "RPT-101", "type", "Complete Blood Count", "date", "2026-06-20", "status", "Final"),
            Map.of("id", "RPT-102", "type", "Chest X-Ray", "date", "2026-06-21", "status", "Final")
        ));
        
        return dashboard;
    }
}
