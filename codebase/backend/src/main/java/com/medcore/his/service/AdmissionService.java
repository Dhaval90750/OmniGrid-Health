package com.medcore.his.service;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.clinical.Admission;
import com.medcore.his.domain.master.Bed;
import com.medcore.his.domain.master.Ward;
import com.medcore.his.domain.patient.Patient;
import com.medcore.his.repository.AdmissionRepository;
// Imports for Patient, Bed, Ward, User repos would go here in a full app
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdmissionService {

    private final AdmissionRepository admissionRepository;

    @Autowired
    public AdmissionService(AdmissionRepository admissionRepository) {
        this.admissionRepository = admissionRepository;
    }

    @Transactional
    public Admission admitPatient(Admission admission) {
        // In a real implementation:
        // 1. Fetch Patient, Doctor, Ward, Bed from DB by ID
        // 2. Check if Bed is AVAILABLE
        // 3. Update Bed status to OCCUPIED (bed.setStatus("OCCUPIED"); bedRepository.save(bed);)
        
        admission.setAdmissionDate(LocalDateTime.now());
        admission.setStatus("ADMITTED");
        
        return admissionRepository.save(admission);
    }

    @Transactional(readOnly = true)
    public List<Admission> getActiveAdmissions() {
        return admissionRepository.findByStatusOrderByAdmissionDateDesc("ADMITTED");
    }
}
