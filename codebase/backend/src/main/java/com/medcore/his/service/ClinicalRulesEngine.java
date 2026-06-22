package com.medcore.his.service;

import com.medcore.his.domain.patient.Allergy;
import com.medcore.his.domain.patient.Patient;
import com.medcore.his.repository.AllergyRepository;
import com.medcore.his.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClinicalRulesEngine {

    private final PatientRepository patientRepository;
    private final AllergyRepository allergyRepository;

    @Autowired
    public ClinicalRulesEngine(PatientRepository patientRepository, AllergyRepository allergyRepository) {
        this.patientRepository = patientRepository;
        this.allergyRepository = allergyRepository;
    }

    /**
     * Checks if any of the prescribed drugs trigger an allergy alert for the patient.
     * In a full implementation, this would use an SNOMED or RxNorm ontology mapping.
     */
    public List<String> checkPrescriptionSafety(UUID patientId, List<String> prescribedDrugGenericNames) {
        List<String> alerts = new ArrayList<>();
        Patient patient = patientRepository.findById(patientId).orElse(null);
        
        if (patient == null) {
            return alerts;
        }

        List<Allergy> activeAllergies = allergyRepository.findByPatientIdAndStatus(patientId, "ACTIVE");
        List<String> patientAllergens = activeAllergies.stream()
                .map(a -> a.getAllergen().toLowerCase())
                .collect(Collectors.toList());

        for (String drug : prescribedDrugGenericNames) {
            String lowerDrug = drug.toLowerCase();
            
            // Check direct allergy match
            for (String allergen : patientAllergens) {
                if (lowerDrug.contains(allergen) || allergen.contains(lowerDrug)) {
                    alerts.add("WARNING: Patient has an active allergy to '" + allergen + "'. Prescribing '" + drug + "' is contraindicated.");
                }
            }
            
            // Cross-reactivity hardcoded for demonstration (e.g. penicillin -> amoxicillin)
            if (lowerDrug.contains("amoxicillin") && patientAllergens.stream().anyMatch(a -> a.contains("penicillin"))) {
                alerts.add("WARNING: Patient has Penicillin allergy. 'Amoxicillin' may cause cross-reactivity.");
            }
        }
        
        // Drug-Drug interactions (example rules)
        boolean hasParacetamol = prescribedDrugGenericNames.stream().anyMatch(d -> d.toLowerCase().contains("paracetamol"));
        boolean hasIbuprofen = prescribedDrugGenericNames.stream().anyMatch(d -> d.toLowerCase().contains("ibuprofen"));
        if (hasParacetamol && hasIbuprofen) {
            alerts.add("CAUTION: Concurrent use of Paracetamol and Ibuprofen may increase risk of hepatotoxicity if overdosed.");
        }

        return alerts;
    }
}
