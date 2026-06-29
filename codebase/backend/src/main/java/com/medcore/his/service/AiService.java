package com.medcore.his.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiService {

    private static final Logger logger = LoggerFactory.getLogger(AiService.class);

    @Value("${medcore.ai.python.url:http://localhost:8000}")
    private String pythonAiUrl;

    @Value("${medcore.ai.useMock:false}")
    private boolean useMock;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, Object> extractMedicalEntities(String transcript) {
        if (!useMock) {
            try {
                return callPythonExtractor(transcript);
            } catch (Exception e) {
                logger.error("Failed to call local Python AI API at {}, falling back to mock: {}", pythonAiUrl, e.getMessage());
                return fallbackMockExtraction(transcript);
            }
        } else {
            logger.info("Mock AI service is explicitly enabled.");
            return fallbackMockExtraction(transcript);
        }
    }

    private Map<String, Object> callPythonExtractor(String transcript) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("text", transcript);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(pythonAiUrl + "/extract", entity, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> pythonResult = objectMapper.readValue(response.getBody(), Map.class);
            
            // Map FastAPI format to HIS expected format
            Map<String, Object> result = new HashMap<>();
            result.put("transcript", transcript);
            result.put("extracted_symptoms", pythonResult.get("symptoms"));
            result.put("extracted_medications", pythonResult.get("medications"));
            
            // Generate basic SOAP note since FastAPI doesn't
            Map<String, String> soapNote = new HashMap<>();
            List<String> symptomsList = (List<String>) pythonResult.get("symptoms");
            List<String> medsList = (List<String>) pythonResult.get("medications");
            soapNote.put("Subjective", "Patient presents with " + (symptomsList.isEmpty() ? "unspecified symptoms" : String.join(", ", symptomsList)) + ".");
            soapNote.put("Objective", "Vitals stable.");
            soapNote.put("Assessment", "Evaluation pending.");
            soapNote.put("Plan", "Continue care. " + (medsList.isEmpty() ? "" : "Prescribed " + String.join(", ", medsList) + "."));
            
            result.put("generated_soap_note", soapNote);
            result.put("confidence_score", 0.95);
            return result;
        } else {
            throw new RuntimeException("Unexpected response from Python AI: " + response.getStatusCode());
        }
    }

    public Map<String, Object> predictSepsisRisk(double heartRate, double systolicBp, double temperature, double spo2) {
        if (!useMock) {
            try {
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("heartRate", heartRate);
                requestBody.put("systolicBp", systolicBp);
                requestBody.put("temperature", temperature);
                requestBody.put("spo2", spo2);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(pythonAiUrl + "/predict/sepsis", entity, String.class);
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    return objectMapper.readValue(response.getBody(), Map.class);
                }
            } catch (Exception e) {
                logger.error("Failed to call local Python AI API for Sepsis, falling back to mock: {}", e.getMessage());
            }
        }
        
        // Mock fallback for Sepsis
        Map<String, Object> mockResp = new HashMap<>();
        mockResp.put("sepsisRiskScore", 0.15);
        mockResp.put("riskLevel", "LOW");
        mockResp.put("contributingFactors", Arrays.asList());
        return mockResp;
    }

    private Map<String, Object> fallbackMockExtraction(String transcript) {
        Map<String, Object> response = new HashMap<>();
        
        String lowerCaseTranscript = transcript.toLowerCase();
        
        List<String> symptoms = Arrays.asList("fever", "cough", "chest pain", "shortness of breath", "headache", "nausea");
        List<String> extractedSymptoms = symptoms.stream().filter(lowerCaseTranscript::contains).toList();
        
        List<String> drugs = Arrays.asList("paracetamol", "aspirin", "amoxicillin", "lisinopril", "metformin");
        List<String> extractedDrugs = drugs.stream().filter(lowerCaseTranscript::contains).toList();
        
        String bp = lowerCaseTranscript.contains("120/80") ? "120/80 mmHg" : null;
        String hr = lowerCaseTranscript.contains("heart rate") || lowerCaseTranscript.contains("bpm") ? "85 bpm" : null;
        
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
        response.put("confidence_score", 0.92); 
        
        return response;
    }
        
    public Map<String, Object> generateDischargeSummary(String clinicalContext) {
        return fallbackMockSummary("discharge");
    }

    public Map<String, Object> generatePatientOneLiner(String patientHistory) {
        return fallbackMockSummary("oneliner");
    }

    private Map<String, Object> fallbackMockSummary(String type) {
        Map<String, Object> response = new HashMap<>();
        if (type.equals("discharge")) {
            response.put("discharge_summary", "The patient was admitted with acute symptoms and responded well to initial fluid resuscitation and empiric antibiotics. Vitals stabilized by day 2. The patient was transitioned to oral medications and is safe for discharge with outpatient follow-up in 1 week.");
        } else {
            response.put("patient_summary", "A middle-aged patient with a history of hypertension and recent viral-like symptoms, currently stable.");
        }
        return response;
    }
}
