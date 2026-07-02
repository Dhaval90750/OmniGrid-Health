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
    private final com.medcore.his.repository.DrugInteractionRepository interactionRepository;

    @Autowired
    public ClinicalRulesEngine(PatientRepository patientRepository, 
                               AllergyRepository allergyRepository,
                               com.medcore.his.repository.DrugInteractionRepository interactionRepository) {
        this.patientRepository = patientRepository;
        this.allergyRepository = allergyRepository;
        this.interactionRepository = interactionRepository;
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

        for (int i = 0; i < prescribedDrugGenericNames.size(); i++) {
            String drug = prescribedDrugGenericNames.get(i);
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

            // Drug-Drug interactions (DB-driven)
            for (int j = i + 1; j < prescribedDrugGenericNames.size(); j++) {
                String otherDrug = prescribedDrugGenericNames.get(j).toLowerCase();
                
                // Check both directions
                List<com.medcore.his.domain.pharmacy.DrugInteraction> interactions = interactionRepository.findByPrimaryDrugGenericIgnoreCaseAndSecondaryDrugGenericIgnoreCase(lowerDrug, otherDrug);
                interactions.addAll(interactionRepository.findByPrimaryDrugGenericIgnoreCaseAndSecondaryDrugGenericIgnoreCase(otherDrug, lowerDrug));
                
                for (com.medcore.his.domain.pharmacy.DrugInteraction interaction : interactions) {
                    alerts.add(interaction.getSeverity() + ": " + interaction.getDescription());
                }
            }
        }
        
        // Dose Range / Therapy duplication checking
        long paracetamolCount = prescribedDrugGenericNames.stream().filter(d -> d.toLowerCase().contains("paracetamol")).count();
        if (paracetamolCount > 1) {
            alerts.add("WARNING: Therapeutic duplication. Multiple formulations containing Paracetamol prescribed.");
        }

        return alerts;
    }
}
