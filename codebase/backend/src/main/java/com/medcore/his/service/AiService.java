package com.medcore.his.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

@Service
public class AiService {

    /**
     * Mocks a Medical NER (Named Entity Recognition) pipeline.
     * In production, this would send the transcript to a fine-tuned LLM or HuggingFace API.
     */
    public Map<String, Object> extractMedicalEntities(String transcript) {
        Map<String, Object> response = new HashMap<>();
        
        // Simple mock extraction based on keywords
        String lowerCaseTranscript = transcript.toLowerCase();
        
        // MOCK SYMPTOMS
        List<String> symptoms = Arrays.asList("fever", "cough", "chest pain", "shortness of breath", "headache", "nausea");
        List<String> extractedSymptoms = symptoms.stream().filter(lowerCaseTranscript::contains).toList();
        
        // MOCK MEDICATIONS
        List<String> drugs = Arrays.asList("paracetamol", "aspirin", "amoxicillin", "lisinopril", "metformin");
        List<String> extractedDrugs = drugs.stream().filter(lowerCaseTranscript::contains).toList();
        
        // MOCK VITALS
        String bp = lowerCaseTranscript.contains("120/80") ? "120/80 mmHg" : null;
        String hr = lowerCaseTranscript.contains("heart rate") || lowerCaseTranscript.contains("bpm") ? "85 bpm" : null;
        
        // MOCK SOAP Note Generation
        Map<String, String> soapNote = new HashMap<>();
        soapNote.put("Subjective", "Patient presents with " + String.join(", ", extractedSymptoms) + ".");
        soapNote.put("Objective", "Vitals stable. BP: " + (bp != null ? bp : "Not recorded") + ". HR: " + (hr != null ? hr : "Not recorded") + ".");
        soapNote.put("Assessment", "Likely viral infection or minor ailment given symptoms.");
        soapNote.put("Plan", "Prescribed " + (extractedDrugs.isEmpty() ? "rest and hydration." : String.join(", ", extractedDrugs)) + ".");
        
        response.put("transcript", transcript);
        response.put("extracted_symptoms", extractedSymptoms);
        response.put("extracted_medications", extractedDrugs);
        response.put("vitals", Map.of("blood_pressure", bp != null ? bp : "N/A", "heart_rate", hr != null ? hr : "N/A"));
        response.put("generated_soap_note", soapNote);
        response.put("confidence_score", 0.92); // Mock AI confidence
        
        return response;
    }
}
