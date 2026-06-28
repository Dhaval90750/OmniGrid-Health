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

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Map;

@Service
public class PaymentGatewayService {

    private final RestTemplate restTemplate;

    @Value("${stripe.secret.key:}")
    private String stripeSecretKey;

    public PaymentGatewayService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Creates a Stripe Payment Intent using real REST API calls.
     */
    public String createPaymentIntent(BigDecimal amount, String currency, String receiptEmail) {
        if (stripeSecretKey == null || stripeSecretKey.isEmpty()) {
            throw new IllegalStateException("Stripe Secret Key not configured");
        }

        String url = "https://api.stripe.com/v1/payment_intents";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        String auth = stripeSecretKey + ":";
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        headers.set("Authorization", "Basic " + new String(encodedAuth));

        // Stripe requires amount in cents
        long amountInCents = amount.multiply(new BigDecimal(100)).longValue();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("amount", String.valueOf(amountInCents));
        map.add("currency", currency.toLowerCase());
        map.add("receipt_email", receiptEmail);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("client_secret");
            }
        } catch (Exception e) {
            System.err.println("Stripe Payment Intent Failed: " + e.getMessage());
        }
        return null;
    }
}
