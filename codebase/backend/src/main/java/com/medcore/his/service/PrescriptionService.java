package com.medcore.his.service;

import com.medcore.his.domain.clinical.Drug;
import com.medcore.his.domain.clinical.Prescription;
import com.medcore.his.domain.clinical.PrescriptionLine;
import com.medcore.his.repository.DrugRepository;
import com.medcore.his.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final DrugRepository drugRepository;

    @Autowired
    public PrescriptionService(PrescriptionRepository prescriptionRepository, DrugRepository drugRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.drugRepository = drugRepository;
    }

    public List<Drug> searchDrugs(String query) {
        return drugRepository.findByGenericNameContainingIgnoreCaseOrBrandNameContainingIgnoreCase(query, query);
    }

    @Transactional
    public Prescription savePrescription(Prescription prescription) {
        for (PrescriptionLine line : prescription.getLines()) {
            line.setPrescription(prescription);
            // Auto-calculate quantity for simple MVP logic
            if (line.getQuantity() == null || line.getQuantity() == 0) {
                int perDay = parseFrequency(line.getFrequency());
                line.setQuantity(perDay * line.getDurationDays());
            }
        }
        return prescriptionRepository.save(prescription);
    }
    
    private int parseFrequency(String freq) {
        if (freq == null) return 1;
        return switch (freq.toUpperCase()) {
            case "BD", "BID" -> 2;
            case "TDS", "TID" -> 3;
            case "QID" -> 4;
            default -> 1; // OD, SOS, Stat etc. default to 1 per day for math
        };
    }
}
