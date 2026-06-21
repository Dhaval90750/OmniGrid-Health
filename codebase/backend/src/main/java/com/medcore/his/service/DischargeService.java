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

    @Autowired
    public DischargeService(DischargeRepository dischargeRepository, VisitRepository visitRepository) {
        this.dischargeRepository = dischargeRepository;
        this.visitRepository = visitRepository;
    }

    @Transactional
    public Discharge initiateDischarge(Discharge discharge) {
        Discharge saved = dischargeRepository.save(discharge);
        
        // Update the visit status
        if (saved.getVisit() != null) {
            saved.getVisit().setStatus("DISCHARGED");
            visitRepository.save(saved.getVisit());
        }
        
        return saved;
    }
}
