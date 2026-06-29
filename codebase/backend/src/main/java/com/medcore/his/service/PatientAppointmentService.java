package com.medcore.his.service;

import com.medcore.his.domain.clinical.Visit;
import com.medcore.his.repository.VisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PatientAppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(PatientAppointmentService.class);

    private final VisitRepository visitRepository;

    @Autowired
    public PatientAppointmentService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    public Visit bookAppointment(UUID patientId, UUID doctorId, String department, LocalDateTime appointmentTime, String visitType) {
        logger.info("Booking appointment for Patient {} with Doctor {} at {}", patientId, doctorId, appointmentTime);
        
        // In a real scenario, check doctor availability slot
        
        Visit visit = new Visit();
        // Assume patient and doctor fetch
        // visit.setPatient(patient);
        // visit.setAttendingDoctor(doctor);
        // visit.setDepartment(department);
        visit.setVisitDate(appointmentTime);
        visit.setVisitType(visitType);
        visit.setStatus("SCHEDULED");
        
        // return visitRepository.save(visit);
        return visit; // Mock return since we don't fetch full entities here to avoid lazy loading issues in stub
    }
    
    public List<Visit> getUpcomingAppointments(UUID patientId) {
        return visitRepository.findByPatientIdOrderByVisitDateDesc(patientId).stream()
                .filter(v -> v.getVisitDate() != null && v.getVisitDate().isAfter(LocalDateTime.now()))
                .toList();
    }
    
    public Visit cancelAppointment(UUID appointmentId) {
        logger.info("Cancelling appointment {}", appointmentId);
        Visit visit = visitRepository.findById(appointmentId).orElseThrow(() -> new RuntimeException("Appointment not found"));
        visit.setStatus("CANCELLED");
        return visitRepository.save(visit);
    }
}
