package com.medcore.his.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClinicalNlpService {

    private static final Logger logger = LoggerFactory.getLogger(ClinicalNlpService.class);

    private final WebClient webClient;
    private final ConfigurationService configurationService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ClinicalNlpService(WebClient.Builder webClientBuilder, ConfigurationService configurationService) {
        this.webClient = webClientBuilder.build();
        this.configurationService = configurationService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Real implementation of Whisper V3 audio transcription.
     * Transmits the audio byte array to the configured LLM API using multipart/form-data.
     */
    public String transcribeAudio(byte[] audioData) {
        logger.info("Received audio data of size {} bytes for transcription. Sending to Real LLM.", audioData.length);
        
        String apiKey = configurationService.getValue("llm.api.key", "sk-mock");
        String apiUrl = configurationService.getValue("llm.api.url", "https://api.openai.com/v1"); // Assuming base URL without /chat/completions
        
        try {
            org.springframework.core.io.ByteArrayResource resource = new org.springframework.core.io.ByteArrayResource(audioData) {
                @Override
                public String getFilename() {
                    return "audio.webm";
                }
            };
            
            org.springframework.util.LinkedMultiValueMap<String, Object> parts = new org.springframework.util.LinkedMultiValueMap<>();
            parts.add("file", resource);
            parts.add("model", "whisper-1");
            
            Map<String, Object> response = webClient.post()
                    .uri(apiUrl.replace("/chat/completions", "/audio/transcriptions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA)
                    .body(org.springframework.web.reactive.function.BodyInserters.fromMultipartData(parts))
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
                    
            if (response != null && response.containsKey("text")) {
                return (String) response.get("text");
            }
        } catch (Exception e) {
            logger.error("Error communicating with Audio Transcription API: {}", e.getMessage());
        }
        
        return "Audio transcription failed due to API error.";
    }

    /**
     * Real implementation of Medical NER using a dynamically configured LLM.
     */
    public Map<String, List<Map<String, String>>> extractEntities(String text) {
        logger.info("Extracting medical entities from text using Real LLM: {}", text);
        
        String systemPrompt = "You are an advanced medical NLP engine. Extract entities from the clinical text. " +
                "Return ONLY a pure JSON object (no markdown) with the following exact keys: " +
                "'Symptoms', 'Vitals', 'Conditions', 'Orders'. Each key must map to an array of objects describing the entity.";
                
        String jsonResponse = callLlmApi(systemPrompt, text);
        
        try {
            return objectMapper.readValue(jsonResponse, new TypeReference<Map<String, List<Map<String, String>>>>() {});
        } catch (Exception e) {
            logger.error("Failed to parse LLM NER response", e);
            return new HashMap<>(); // Fallback empty
        }
    }
    
    /**
     * Real implementation of SOAP Note Generator using a dynamically configured LLM.
     */
    public Map<String, String> generateSoapNote(String text, Map<String, Object> patientContext) {
        logger.info("Generating SOAP note using Real LLM");
        
        String systemPrompt = "You are a clinical AI assistant. Given the doctor's unstructured transcription and patient context, " +
                "generate a structured SOAP note. Return ONLY a pure JSON object (no markdown) with exactly 4 keys: " +
                "'Subjective', 'Objective', 'Assessment', 'Plan'. Each key must map to a string.";
                
        String userContext = "Patient Context: " + patientContext.toString() + "\n\nTranscription: " + text;
        String jsonResponse = callLlmApi(systemPrompt, userContext);
        
        try {
            return objectMapper.readValue(jsonResponse, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            logger.error("Failed to parse LLM SOAP response", e);
            return new HashMap<>();
        }
    }
    
    /**
     * Real implementation of Intelligent ICD-10 Search using a dynamically configured LLM.
     */
    public List<Map<String, String>> suggestIcd10Codes(String clinicalText) {
        logger.info("Suggesting ICD-10 codes using Real LLM for text: {}", clinicalText);
        
        String systemPrompt = "You are a specialized medical coding AI. Map the provided clinical diagnosis or symptoms to the most accurate ICD-10-CM codes. " +
                "Return ONLY a pure JSON array of objects (no markdown) with exact keys: 'code' (the ICD-10 code) and 'description' (the official description). " +
                "Limit to top 5 most relevant codes.";
                
        String jsonResponse = callLlmApi(systemPrompt, clinicalText);
        
        try {
            return objectMapper.readValue(jsonResponse, new TypeReference<List<Map<String, String>>>() {});
        } catch (Exception e) {
            logger.error("Failed to parse LLM ICD-10 response", e);
            return List.of();
        }
    }
    
    /**
     * Reusable method to call the generic LLM Chat Completions API based on dynamic Configuration.
     */
    private String callLlmApi(String systemPrompt, String userMessage) {
        String apiKey = configurationService.getValue("llm.api.key", "sk-mock");
        String apiUrl = configurationService.getValue("llm.api.url", "https://api.openai.com/v1/chat/completions");
        String model = configurationService.getValue("llm.model", "gpt-4o-mini");
        
        // Construct standard OpenAI-compatible Chat Completion Payload
        Map<String, Object> payload = Map.of(
            "model", model,
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
            ),
            "temperature", 0.0 // Zero temperature for strict formatting
        );
        
        try {
            Map<String, Object> response = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
                    
            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    // Strip potential markdown blocks just in case
                    if (content.startsWith("```json")) {
                        content = content.replace("```json", "").replace("```", "").trim();
                    }
                    return content;
                }
            }
        } catch (Exception e) {
            logger.error("Error communicating with LLM API: {}", e.getMessage());
            // Return empty JSON object string on failure
            return "{}";
        }
        
        return "{}";
    }
}
