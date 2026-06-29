package com.medcore.his.service;

import com.medcore.his.domain.bloodbank.BloodBag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BloodCrossMatchingService {

    private static final Logger logger = LoggerFactory.getLogger(BloodCrossMatchingService.class);

    /**
     * Standard ABO & Rh compatibility rules for PRBCs.
     * Returns true if donor blood can be safely given to the patient.
     */
    public boolean isCompatible(String donorGroup, String patientGroup) {
        if (donorGroup.equals("O-")) return true; // Universal Donor
        if (patientGroup.equals("AB+")) return true; // Universal Recipient

        boolean rhCompatible = checkRhCompatibility(donorGroup, patientGroup);
        boolean aboCompatible = checkAboCompatibility(donorGroup, patientGroup);

        return rhCompatible && aboCompatible;
    }

    private boolean checkRhCompatibility(String donor, String patient) {
        boolean donorRh = donor.endsWith("+");
        boolean patientRh = patient.endsWith("+");
        
        // Rh- can give to Rh+ and Rh-
        // Rh+ can only give to Rh+
        if (donorRh && !patientRh) return false;
        return true;
    }

    private boolean checkAboCompatibility(String donor, String patient) {
        String d = donor.substring(0, donor.length() - 1);
        String p = patient.substring(0, patient.length() - 1);

        if (d.equals("O")) return true;
        if (d.equals(p)) return true;
        if (p.equals("AB") && (d.equals("A") || d.equals("B"))) return true;
        
        return false;
    }

    public BloodBag allocateCompatibleBlood(String patientBloodGroup, List<BloodBag> availableInventory) {
        for (BloodBag bag : availableInventory) {
            if (bag.getStatus().equals("AVAILABLE") && isCompatible(bag.getBloodGroup(), patientBloodGroup)) {
                logger.info("Allocating Blood Bag {} ({}) for Patient ({})", bag.getBagNumber(), bag.getBloodGroup(), patientBloodGroup);
                bag.setStatus("RESERVED");
                return bag; // In real impl, would save to repo
            }
        }
        throw new RuntimeException("No compatible blood available in inventory for group: " + patientBloodGroup);
    }
}
