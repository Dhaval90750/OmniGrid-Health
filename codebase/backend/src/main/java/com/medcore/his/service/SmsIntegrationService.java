package com.medcore.his.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class SmsIntegrationService {

    private final RestTemplate restTemplate;

    @Value("${twilio.account.sid:}")
    private String accountSid;

    @Value("${twilio.auth.token:}")
    private String authToken;

    @Value("${twilio.phone.number:}")
    private String fromPhoneNumber;

    public SmsIntegrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean sendSms(String toPhoneNumber, String message) {
        if (accountSid == null || accountSid.isEmpty()) {
            System.err.println("SMS Failed: Twilio Account SID not configured.");
            return false;
        }

        String url = "https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        String auth = accountSid + ":" + authToken;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        headers.set("Authorization", "Basic " + new String(encodedAuth));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("To", toPhoneNumber);
        map.add("From", fromPhoneNumber);
        map.add("Body", message);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("SMS Failed: " + e.getMessage());
            return false;
        }
    }
}
