package com.medcore.his.service;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.clinical.Admission;
import com.medcore.his.domain.master.Bed;
import com.medcore.his.domain.master.Ward;
import com.medcore.his.domain.patient.Patient;
import com.medcore.his.repository.AdmissionRepository;
import com.medcore.his.repository.BedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdmissionService {

    private final AdmissionRepository admissionRepository;
    private final BedRepository bedRepository;

    @Autowired
    public AdmissionService(AdmissionRepository admissionRepository, BedRepository bedRepository) {
        this.admissionRepository = admissionRepository;
        this.bedRepository = bedRepository;
    }

    @Transactional
    public Admission admitPatient(Admission admission) {
        if (admission.getBed() != null && admission.getBed().getId() != null) {
            Bed bed = bedRepository.findById(admission.getBed().getId())
                    .orElseThrow(() -> new RuntimeException("Bed not found"));
            
            if (!"AVAILABLE".equals(bed.getStatus())) {
                throw new RuntimeException("Bed is not available for admission");
            }
            
            bed.setStatus("OCCUPIED");
            bedRepository.save(bed);
        }
        
        admission.setAdmissionDate(LocalDateTime.now());
        admission.setStatus("ADMITTED");
        
        return admissionRepository.save(admission);
    }

    @Transactional(readOnly = true)
    public List<Admission> getActiveAdmissions() {
        return admissionRepository.findByStatusOrderByAdmissionDateDesc("ADMITTED");
    }

    @Transactional
    public Admission dischargePatient(java.util.UUID admissionId, String dischargeSummary) {
        Admission admission = admissionRepository.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));
                
        if (!"ADMITTED".equals(admission.getStatus())) {
            throw new RuntimeException("Patient is not actively admitted");
        }
        
        admission.setStatus("DISCHARGED");
        admission.setDischargeDate(LocalDateTime.now());
        admission.setDischargeSummary(dischargeSummary);
        
        if (admission.getBed() != null) {
            Bed bed = admission.getBed();
            bed.setStatus("CLEANING");
            bedRepository.save(bed);
        }
        
        return admissionRepository.save(admission);
    }

    @Transactional
    public Bed markBedClean(java.util.UUID bedId) {
        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new RuntimeException("Bed not found"));
                
        if (!"CLEANING".equals(bed.getStatus())) {
            throw new RuntimeException("Bed is not in CLEANING state");
        }
        
        bed.setStatus("AVAILABLE");
        return bedRepository.save(bed);
    }

    @Transactional
    public Admission transferPatient(java.util.UUID admissionId, java.util.UUID newBedId) {
        Admission admission = admissionRepository.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));
                
        if (!"ADMITTED".equals(admission.getStatus())) {
            throw new RuntimeException("Patient is not actively admitted");
        }
        
        Bed newBed = bedRepository.findById(newBedId)
                .orElseThrow(() -> new RuntimeException("New bed not found"));
                
        if (!"AVAILABLE".equals(newBed.getStatus())) {
            throw new RuntimeException("New bed is not available");
        }
        
        if (admission.getBed() != null) {
            Bed oldBed = admission.getBed();
            oldBed.setStatus("CLEANING");
            bedRepository.save(oldBed);
        }
        
        newBed.setStatus("OCCUPIED");
        bedRepository.save(newBed);
        
        admission.setBed(newBed);
        if (newBed.getWard() != null) {
            admission.setWard(newBed.getWard());
        }
        
        return admissionRepository.save(admission);
    }
    
    @Transactional(readOnly = true)
    public Admission getAdmission(java.util.UUID admissionId) {
        return admissionRepository.findById(admissionId).orElse(null);
    }
}
