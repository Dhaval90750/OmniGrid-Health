package com.medcore.his.service;

import com.medcore.his.domain.discharge.Discharge;
import com.medcore.his.repository.DischargeRepository;
import com.medcore.his.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DischargeService {

    private final DischargeRepository dischargeRepository;
    private final VisitRepository visitRepository;
    private final AdmissionService admissionService;

    @Autowired
    public DischargeService(DischargeRepository dischargeRepository, VisitRepository visitRepository, AdmissionService admissionService) {
        this.dischargeRepository = dischargeRepository;
        this.visitRepository = visitRepository;
        this.admissionService = admissionService;
    }

    @Transactional
    public Discharge initiateDischarge(Discharge discharge) {
        Discharge saved = dischargeRepository.save(discharge);
        
        // Update the visit status
        if (saved.getVisit() != null) {
            saved.getVisit().setStatus("DISCHARGED");
            visitRepository.save(saved.getVisit());
            
            // Link to admission lifecycle if it exists
            if (saved.getVisit().getAdmission() != null) {
                admissionService.dischargePatient(saved.getVisit().getAdmission().getId(), saved.getFinalSummary());
            }
        }
        
        return saved;
    }
}
