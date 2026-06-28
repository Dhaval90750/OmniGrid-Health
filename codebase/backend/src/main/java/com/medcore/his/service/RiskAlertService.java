package com.medcore.his.service;

import com.medcore.his.domain.nursing.PatientVital;
import com.medcore.his.repository.PatientVitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RiskAlertService {

    private final PatientVitalRepository patientVitalRepository;

    @Autowired
    public RiskAlertService(PatientVitalRepository patientVitalRepository) {
        this.patientVitalRepository = patientVitalRepository;
    }

    /**
     * Evaluates the risk of Sepsis based on the Systemic Inflammatory Response Syndrome (SIRS) criteria.
     * Criteria:
     * - Temperature > 38°C or < 36°C
     * - Heart Rate > 90 bpm
     * - Respiratory Rate > 20 breaths/min
     * Note: WBC count is normally included but omitted here for vitals-only check.
     */
    public Map<String, Object> evaluateSepsisRisk(UUID patientId) {
        // Fetch the most recent vitals for the patient
        List<PatientVital> vitalsList = patientVitalRepository.findByPatientIdOrderByRecordedAtDesc(patientId);
        
        Map<String, Object> result = new HashMap<>();
        if (vitalsList == null || vitalsList.isEmpty()) {
            result.put("riskLevel", "UNKNOWN");
            result.put("message", "No vitals recorded for this patient.");
            return result;
        }

        PatientVital latestVitals = vitalsList.get(0);
        int sirsScore = 0;
        StringBuilder riskFactors = new StringBuilder();

        // 1. Temperature
        if (latestVitals.getTemperature() != null) {
            double temp = latestVitals.getTemperature().doubleValue();
            if (temp > 38.0 || temp < 36.0) {
                sirsScore++;
                riskFactors.append("Abnormal Temperature (").append(temp).append("°C). ");
            }
        }

        // 2. Heart Rate
        if (latestVitals.getHeartRate() != null && latestVitals.getHeartRate() > 90) {
            sirsScore++;
            riskFactors.append("Elevated Heart Rate (").append(latestVitals.getHeartRate()).append(" bpm). ");
        }

        // 3. Respiratory Rate
        if (latestVitals.getRespiratoryRate() != null && latestVitals.getRespiratoryRate() > 20) {
            sirsScore++;
            riskFactors.append("Elevated Respiratory Rate (").append(latestVitals.getRespiratoryRate()).append(" breaths/min). ");
        }

        result.put("sirsScore", sirsScore);
        result.put("factors", riskFactors.toString().trim());

        if (sirsScore >= 2) {
            result.put("riskLevel", "HIGH");
            result.put("message", "High risk of Sepsis detected based on SIRS criteria.");
        } else if (sirsScore == 1) {
            result.put("riskLevel", "MODERATE");
            result.put("message", "Moderate risk of Sepsis. Monitor patient closely.");
        } else {
            result.put("riskLevel", "LOW");
            result.put("message", "Low risk of Sepsis.");
        }

        return result;
    }

    /**
     * Evaluates readmission risk based on a simplified LACE heuristic.
     * For this prototype, we'll use a mocked calculation based on length of stay if provided,
     * or general patient metrics.
     */
    public Map<String, Object> evaluateReadmissionRisk(UUID patientId, int lengthOfStayDays, boolean isEmergency) {
        Map<String, Object> result = new HashMap<>();
        
        int laceScore = 0;
        
        // L: Length of Stay (1-4 days = score 1-4, >4 days = max score)
        laceScore += Math.min(lengthOfStayDays, 7);
        
        // A: Acuity of Admission
        if (isEmergency) {
            laceScore += 3;
        }
        
        // C & E would normally be calculated from historical visits/comorbidities.
        // We add a baseline heuristic here for the prototype.
        
        result.put("laceScore", laceScore);
        
        if (laceScore >= 10) {
            result.put("riskLevel", "HIGH");
            result.put("message", "High risk of readmission within 30 days.");
        } else if (laceScore >= 5) {
            result.put("riskLevel", "MODERATE");
            result.put("message", "Moderate risk of readmission.");
        } else {
            result.put("riskLevel", "LOW");
            result.put("message", "Low risk of readmission.");
        }
        
        return result;
    }
}
