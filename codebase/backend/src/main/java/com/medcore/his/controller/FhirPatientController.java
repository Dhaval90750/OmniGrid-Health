package com.medcore.his.controller;

import com.medcore.his.domain.patient.Patient;
import com.medcore.his.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/fhir")
public class FhirPatientController {

    private final PatientRepository patientRepository;

    @Autowired
    public FhirPatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Exposes MedCore Patient data in the standard HL7 FHIR R4 JSON format.
     */
    @GetMapping("/Patient/{uhid}")
    public ResponseEntity<Map<String, Object>> getFhirPatient(@PathVariable String uhid) {
        Optional<Patient> patientOpt = patientRepository.findByUhid(uhid);
        if (patientOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Patient patient = patientOpt.get();
        Map<String, Object> fhirPatient = new HashMap<>();

        // 1. Base FHIR Metadata
        fhirPatient.put("resourceType", "Patient");
        fhirPatient.put("id", patient.getUhid());

        // 2. Identifiers (UHID + ABHA)
        List<Map<String, Object>> identifiers = new ArrayList<>();
        
        // UHID (Medical Record Number)
        Map<String, Object> mrn = new HashMap<>();
        mrn.put("use", "usual");
        mrn.put("type", Map.of("coding", List.of(Map.of("system", "http://terminology.hl7.org/CodeSystem/v2-0203", "code", "MR"))));
        mrn.put("system", "https://medcore.local/fhir/NamingSystem/uhid");
        mrn.put("value", patient.getUhid());
        identifiers.add(mrn);
        
        // ABHA (if linked)
        if (patient.getAbhaAddress() != null) {
            Map<String, Object> abha = new HashMap<>();
            abha.put("use", "official");
            abha.put("type", Map.of("coding", List.of(Map.of("system", "https://nrces.in/ndhm/fhir/r4/CodeSystem/ndhm-identifier-type-code", "code", "ABHA"))));
            abha.put("system", "https://healthid.ndhm.gov.in");
            abha.put("value", patient.getAbhaAddress());
            identifiers.add(abha);
        }
        
        fhirPatient.put("identifier", identifiers);

        // 3. Name
        Map<String, Object> name = new HashMap<>();
        name.put("use", "official");
        name.put("text", patient.getFirstName() + " " + patient.getLastName());
        name.put("family", patient.getLastName());
        name.put("given", List.of(patient.getFirstName()));
        fhirPatient.put("name", List.of(name));

        // 4. Demographics
        fhirPatient.put("gender", patient.getGender().toLowerCase());
        if (patient.getDateOfBirth() != null) {
            fhirPatient.put("birthDate", patient.getDateOfBirth().toString()); // YYYY-MM-DD
        }

        // 5. Telecom
        List<Map<String, Object>> telecoms = new ArrayList<>();
        if (patient.getMobileNumber() != null) {
            telecoms.add(Map.of("system", "phone", "value", patient.getMobileNumber(), "use", "mobile"));
        }
        if (patient.getEmail() != null) {
            telecoms.add(Map.of("system", "email", "value", patient.getEmail(), "use", "home"));
        }
        if (!telecoms.isEmpty()) {
            fhirPatient.put("telecom", telecoms);
        }

        // 6. Address
        if (patient.getCity() != null || patient.getState() != null) {
            Map<String, Object> address = new HashMap<>();
            address.put("use", "home");
            
            List<String> lines = new ArrayList<>();
            if (patient.getAddressLine1() != null) lines.add(patient.getAddressLine1());
            if (patient.getAddressLine2() != null) lines.add(patient.getAddressLine2());
            if (!lines.isEmpty()) address.put("line", lines);
            
            if (patient.getCity() != null) address.put("city", patient.getCity());
            if (patient.getState() != null) address.put("state", patient.getState());
            if (patient.getZipCode() != null) address.put("postalCode", patient.getZipCode());
            if (patient.getCountry() != null) address.put("country", patient.getCountry());
            
            fhirPatient.put("address", List.of(address));
        }

        return ResponseEntity.ok(fhirPatient);
    }
}
