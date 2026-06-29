package com.medcore.his.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PredictiveAnalyticsService {

    /**
     * Calculates Sepsis Risk based on the Systemic Inflammatory Response Syndrome (SIRS) criteria.
     * Requires at least 2 of the following to be flagged for high risk:
     * - Temperature > 38°C or < 36°C
     * - Heart Rate > 90 bpm
     * - Respiratory Rate > 20 breaths/min
     * - WBC count > 12,000 or < 4,000 /mm3 (assumed normal if not provided here)
     */
    public Map<String, Object> calculateSepsisRisk(double temperature, int heartRate, int respiratoryRate) {
        int sirsScore = 0;
        
        if (temperature > 38.0 || temperature < 36.0) sirsScore++;
        if (heartRate > 90) sirsScore++;
        if (respiratoryRate > 20) sirsScore++;

        Map<String, Object> riskResult = new HashMap<>();
        riskResult.put("sirsScore", sirsScore);
        
        if (sirsScore >= 2) {
            riskResult.put("riskLevel", "HIGH");
            riskResult.put("message", "SIRS criteria met (Score: " + sirsScore + "). Evaluate for Sepsis immediately.");
        } else if (sirsScore == 1) {
            riskResult.put("riskLevel", "MODERATE");
            riskResult.put("message", "One SIRS criterion met. Monitor closely.");
        } else {
            riskResult.put("riskLevel", "LOW");
            riskResult.put("message", "Patient does not meet SIRS criteria for Sepsis.");
        }
        
        return riskResult;
    }

    /**
     * Calculates a 30-day readmission risk based on clinical heuristics (LACE index approximation).
     * L: Length of stay
     * A: Acuity of admission
     * C: Comorbidities (Charlson comorbidity index)
     * E: Emergency visits in past 6 months
     */
    public Map<String, Object> calculateReadmissionRisk(int lengthOfStayDays, boolean isEmergencyAdmission, int numComorbidities, int erVisitsPast6Months) {
        int score = 0;
        
        // Length of stay scoring
        if (lengthOfStayDays >= 1 && lengthOfStayDays <= 3) score += lengthOfStayDays;
        else if (lengthOfStayDays >= 4 && lengthOfStayDays <= 6) score += 4;
        else if (lengthOfStayDays >= 7 && lengthOfStayDays <= 13) score += 5;
        else if (lengthOfStayDays >= 14) score += 7;
        
        // Acuity
        if (isEmergencyAdmission) score += 3;
        
        // Comorbidities
        if (numComorbidities == 1) score += 1;
        else if (numComorbidities == 2) score += 2;
        else if (numComorbidities == 3) score += 3;
        else if (numComorbidities >= 4) score += 5;
        
        // ER Visits
        if (erVisitsPast6Months == 1) score += 1;
        else if (erVisitsPast6Months == 2) score += 2;
        else if (erVisitsPast6Months == 3) score += 3;
        else if (erVisitsPast6Months >= 4) score += 4;

        Map<String, Object> result = new HashMap<>();
        result.put("laceScore", score);
        
        if (score >= 10) {
            result.put("riskLevel", "HIGH");
            result.put("probabilityPercent", 30 + (score - 10) * 5); // Rough approximation
        } else if (score >= 5) {
            result.put("riskLevel", "MODERATE");
            result.put("probabilityPercent", 15 + (score - 5) * 2);
        } else {
            result.put("riskLevel", "LOW");
            result.put("probabilityPercent", score * 2);
        }
        
        return result;
    }
}
