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
        return infectionReportRepository.save(report);
    }
}
