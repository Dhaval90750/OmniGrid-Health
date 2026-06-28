package com.medcore.his.job;

import com.medcore.his.domain.auth.User;
import com.medcore.his.repository.UserRepository;
import com.medcore.his.service.SmsIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class CredentialingCronJob {

    private final UserRepository userRepository;
    private final SmsIntegrationService smsIntegrationService;

    @Autowired
    public CredentialingCronJob(UserRepository userRepository, SmsIntegrationService smsIntegrationService) {
        this.userRepository = userRepository;
        this.smsIntegrationService = smsIntegrationService;
    }

    /**
     * Runs every day at 2:00 AM.
     * Finds doctors/staff whose credentials expire in exactly 30 days and sends a reminder.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void checkExpiringCredentials() {
        LocalDate targetDate = LocalDate.now().plusDays(30);
        
        List<User> expiringUsers = userRepository.findByLicenseExpiryDateBetweenAndIsActiveTrue(targetDate, targetDate);
        
        for (User user : expiringUsers) {
            String message = String.format("Dear %s %s, your hospital credentials/license will expire on %s. Please submit your renewal documents to HR.",
                    user.getFirstName(), user.getLastName(), user.getLicenseExpiryDate());
            
            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                smsIntegrationService.sendSms(user.getPhone(), message);
            }
            
            // In a real system, we'd also log this notification in a communications table.
            System.out.println("Re-credentialing alert sent to: " + user.getUsername());
        }
    }
}
