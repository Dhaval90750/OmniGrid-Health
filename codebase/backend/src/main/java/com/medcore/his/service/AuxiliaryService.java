package com.medcore.his.service;

import com.medcore.his.domain.auxiliary.BloodInventory;
import com.medcore.his.domain.auxiliary.InfectionReport;
import com.medcore.his.repository.BloodInventoryRepository;
import com.medcore.his.repository.InfectionReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuxiliaryService {

    private final BloodInventoryRepository bloodInventoryRepository;
    private final InfectionReportRepository infectionReportRepository;

    @Autowired
    public AuxiliaryService(BloodInventoryRepository bloodInventoryRepository, InfectionReportRepository infectionReportRepository) {
        this.bloodInventoryRepository = bloodInventoryRepository;
        this.infectionReportRepository = infectionReportRepository;
    }

    public List<BloodInventory> getAllBloodInventory() {
        return bloodInventoryRepository.findAll();
    }

    @Transactional
    public BloodInventory addBloodUnit(BloodInventory unit) {
        return bloodInventoryRepository.save(unit);
    }

    public List<InfectionReport> getAllInfectionReports() {
        return infectionReportRepository.findAll();
    }

    @Transactional
    public InfectionReport addInfectionReport(InfectionReport report) {
        // Flag for isolation if it's a Hospital Acquired Infection or specific resistant organism
        if ("Active".equals(report.getStatus())) {
            if (report.getInfectionType() != null && report.getInfectionType().contains("CAUTI") || report.getInfectionType().contains("CLABSI") || report.getInfectionType().contains("MRSA")) {
                report.setHai(true);
                report.setRequiresIsolation(true);
            }
        }
        return infectionReportRepository.save(report);
    }
    
    // -- Blood Bank Logic --
    @Autowired
    private com.medcore.his.repository.BloodDonationRepository bloodDonationRepository;
    @Autowired
    private com.medcore.his.repository.BloodTransfusionRepository bloodTransfusionRepository;
    
    public List<com.medcore.his.domain.auxiliary.BloodDonation> getAllDonations() {
        return bloodDonationRepository.findAll();
    }
    
    @Transactional
    public com.medcore.his.domain.auxiliary.BloodDonation addDonation(com.medcore.his.domain.auxiliary.BloodDonation donation) {
        return bloodDonationRepository.save(donation);
    }
    
    public List<com.medcore.his.domain.auxiliary.BloodTransfusion> getAllTransfusions() {
        return bloodTransfusionRepository.findAll();
    }
    
    @Transactional
    public com.medcore.his.domain.auxiliary.BloodTransfusion requestTransfusion(com.medcore.his.domain.auxiliary.BloodTransfusion request) {
        // Cross-match simulation
        if (request.getDonation() != null) {
            String donorGroup = request.getDonation().getBloodGroup();
            String patientGroup = request.getPatient().getBloodGroup();
            // Simplified compatibility logic (in reality, requires complex cross-match rules)
            if (donorGroup.equals(patientGroup) || donorGroup.equals("O-") || patientGroup.equals("AB+")) {
                request.setCrossMatchResult("COMPATIBLE");
                request.setStatus("CROSS_MATCHED");
            } else {
                request.setCrossMatchResult("INCOMPATIBLE");
                request.setStatus("REQUESTED");
            }
        }
        return bloodTransfusionRepository.save(request);
    }
}
