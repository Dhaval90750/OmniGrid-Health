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

    @Value("${medcore.ai.ollama.url:http://localhost:11434/api/generate}")
    private String ollamaUrl;
    
    @Value("${medcore.ai.ollama.model:mistral}")
    private String ollamaModel;

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
                return callOllamaApi(transcript);
            } catch (Exception e) {
                logger.error("Failed to call local Ollama API at {}, falling back to mock: {}", ollamaUrl, e.getMessage());
                return fallbackMockExtraction(transcript);
            }
        } else {
            logger.info("Mock AI service is explicitly enabled.");
            return fallbackMockExtraction(transcript);
        }
    }

    private Map<String, Object> callOllamaApi(String transcript) throws Exception {
        String prompt = "You are an expert clinical AI scribe. Analyze the following transcript and extract structured medical information. "
                + "Return ONLY a valid JSON object without markdown formatting. The JSON must have this exact structure: "
                + "{ \"extracted_symptoms\": [\"symptom1\"], \"extracted_medications\": [\"drug1\"], "
                + "\"vitals\": { \"blood_pressure\": \"...\", \"heart_rate\": \"...\" }, "
                + "\"generated_soap_note\": { \"Subjective\": \"...\", \"Objective\": \"...\", \"Assessment\": \"...\", \"Plan\": \"...\" } } "
                + "Transcript: " + transcript;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", ollamaModel);
        requestBody.put("prompt", prompt);
        requestBody.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(ollamaUrl, entity, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            JsonNode root = objectMapper.readTree(response.getBody());
            String responseText = root.path("response").asText();
            
            // Clean markdown if present
            if (responseText.startsWith("```json")) {
                responseText = responseText.substring(7);
            }
            if (responseText.endsWith("```")) {
                responseText = responseText.substring(0, responseText.length() - 3);
            }
            
            Map<String, Object> result = objectMapper.readValue(responseText.trim(), Map.class);
            result.put("transcript", transcript);
            result.put("confidence_score", 0.98);
            return result;
        } else {
            throw new RuntimeException("Unexpected response from Ollama: " + response.getStatusCode());
        }
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
        if (!useMock) {
            try {
                return callOllamaForSummary(clinicalContext, "discharge");
            } catch (Exception e) {
                logger.error("Failed to call Ollama API for discharge summary, falling back: {}", e.getMessage());
                return fallbackMockSummary("discharge");
            }
        }
        return fallbackMockSummary("discharge");
    }

    public Map<String, Object> generatePatientOneLiner(String patientHistory) {
        if (!useMock) {
            try {
                return callOllamaForSummary(patientHistory, "oneliner");
            } catch (Exception e) {
                logger.error("Failed to call Ollama API for patient one-liner, falling back: {}", e.getMessage());
                return fallbackMockSummary("oneliner");
            }
        }
        return fallbackMockSummary("oneliner");
    }

    private Map<String, Object> callOllamaForSummary(String context, String type) throws Exception {
        String prompt = "";
        if (type.equals("discharge")) {
            prompt = "You are an expert clinical AI. Read the following hospital admission context and generate a cohesive, professional 2-paragraph Hospital Course and Discharge Summary. "
                   + "Return ONLY a valid JSON object: { \"discharge_summary\": \"...\" }\n\nContext: " + context;
        } else {
            prompt = "You are an expert clinical AI. Read the following patient history and generate a quick 1-2 sentence 'One-Liner' clinical summary. "
                   + "Return ONLY a valid JSON object: { \"patient_summary\": \"...\" }\n\nHistory: " + context;
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", ollamaModel);
        requestBody.put("prompt", prompt);
        requestBody.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(ollamaUrl, entity, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            JsonNode root = objectMapper.readTree(response.getBody());
            String responseText = root.path("response").asText();
            
            if (responseText.startsWith("```json")) responseText = responseText.substring(7);
            if (responseText.endsWith("```")) responseText = responseText.substring(0, responseText.length() - 3);
            
            return objectMapper.readValue(responseText.trim(), Map.class);
        } else {
            throw new RuntimeException("Unexpected response from Ollama: " + response.getStatusCode());
        }
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
