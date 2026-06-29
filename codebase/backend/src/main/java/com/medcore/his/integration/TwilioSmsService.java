package com.medcore.his.integration;

import com.medcore.his.service.ConfigurationService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsService {

    private final ConfigurationService configurationService;

    public TwilioSmsService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void sendSms(String toPhoneNumber, String messageBody) {
        String accountSid = configurationService.getValue("twilio.account.sid", "AC_mocked_sid_replace_me");
        String authToken = configurationService.getValue("twilio.auth.token", "mocked_token_replace_me");
        String fromNumber = configurationService.getValue("twilio.phone.number", "+1234567890");

        Twilio.init(accountSid, authToken);

        Message message = Message.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber(fromNumber),
                messageBody
        ).create();
        
        System.out.println("Twilio SMS sent successfully with SID: " + message.getSid());
    }
}
