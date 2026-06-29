package com.medcore.his.integration;

import com.medcore.his.service.ConfigurationService;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentService {

    private final ConfigurationService configurationService;

    public StripePaymentService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public String createPaymentIntent(Long amountInCents, String currency, String receiptEmail) throws Exception {
        String apiKey = configurationService.getValue("stripe.api.key", "sk_test_mocked_key_replace_me");
        Stripe.apiKey = apiKey;

        PaymentIntentCreateParams params =
            PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency)
                .setReceiptEmail(receiptEmail)
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return paymentIntent.getClientSecret();
    }
}
