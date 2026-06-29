package com.medcore.his.integration;

import com.medcore.his.service.ConfigurationService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class AbdmWebClient {

    private final WebClient webClient;
    private final ConfigurationService configurationService;

    public AbdmWebClient(WebClient.Builder webClientBuilder, ConfigurationService configurationService) {
        this.webClient = webClientBuilder.build();
        this.configurationService = configurationService;
    }

    public Mono<String> generateAadhaarOtp(String aadhaarNumber) {
        String abdmBaseUrl = configurationService.getValue("abdm.sandbox.url", "https://dev.abdm.gov.in/gateway/v0.5");
        Map<String, String> requestBody = Map.of("aadhaar", aadhaarNumber);
        
        return webClient.post()
                .uri(abdmBaseUrl + "/sessions/aadhaar/generateOtp")
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("{\"error\": \"Failed to reach ABDM Sandbox: " + e.getMessage() + "\"}"));
    }
}
