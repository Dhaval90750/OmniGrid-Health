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
    private final com.medcore.his.repository.DiagnosisRepository diagnosisRepository;
    private final com.medcore.his.repository.VisitRepository visitRepository;
    private final AiService aiService;

    @Autowired
    public RiskAlertService(PatientVitalRepository patientVitalRepository, 
                            com.medcore.his.repository.DiagnosisRepository diagnosisRepository,
                            com.medcore.his.repository.VisitRepository visitRepository,
                            AiService aiService) {
        this.patientVitalRepository = patientVitalRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.visitRepository = visitRepository;
        this.aiService = aiService;
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
        List<PatientVital> vitalsList = patientVitalRepository.findByPatientIdOrderByRecordedAtDesc(patientId);
        
        if (vitalsList == null || vitalsList.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("riskLevel", "UNKNOWN");
            result.put("message", "No vitals recorded for this patient.");
            return result;
        }

        PatientVital latest = vitalsList.get(0);
        double hr = latest.getHeartRate() != null ? latest.getHeartRate() : 80;
        double sbp = latest.getBloodPressureSystolic() != null ? latest.getBloodPressureSystolic() : 120;
        double temp = latest.getTemperature() != null ? latest.getTemperature().doubleValue() : 37.0;
        double spo2 = latest.getSpo2() != null ? latest.getSpo2().doubleValue() : 98;
        
        Map<String, Object> aiResult = aiService.predictSepsisRisk(hr, sbp, temp, spo2);
        
        Map<String, Object> result = new HashMap<>();
        result.put("riskLevel", aiResult.get("riskLevel"));
        result.put("sepsisRiskScore", aiResult.get("sepsisRiskScore"));
        
        List<String> factors = (List<String>) aiResult.get("contributingFactors");
        factors = factors.stream().filter(f -> !f.isEmpty()).toList();
        
        if (!factors.isEmpty()) {
            result.put("factors", String.join(", ", factors));
        } else {
            result.put("factors", "None");
        }
        
        if ("CRITICAL".equals(aiResult.get("riskLevel")) || "HIGH".equals(aiResult.get("riskLevel"))) {
            result.put("message", "High risk of Sepsis detected by AI model.");
        } else if ("MODERATE".equals(aiResult.get("riskLevel"))) {
            result.put("message", "Moderate risk of Sepsis. Monitor patient closely.");
        } else {
            result.put("message", "Low risk of Sepsis.");
        }
        
        return result;
    }

    /**
     * Evaluates readmission risk based on a simplified LACE heuristic.
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
        
        // C: Comorbidities (Charlson index approximation)
        List<com.medcore.his.domain.clinical.Diagnosis> diagnoses = diagnosisRepository.findByPatientIdAndStatusOrderByDiagnosedDateDesc(patientId, "Active");
        if (diagnoses != null) {
            int comorbiditiesScore = Math.min(diagnoses.size(), 5); // Rough heuristic: 1 point per active diagnosis, max 5
            laceScore += comorbiditiesScore;
        }
        
        // E: Emergency department visits in past 6 months
        java.time.LocalDateTime sixMonthsAgo = java.time.LocalDateTime.now().minusMonths(6);
        List<com.medcore.his.domain.clinical.Visit> visits = visitRepository.findByPatientIdOrderByVisitDateDesc(patientId);
        long edVisits = visits.stream()
                .filter(v -> v.getVisitDate() != null && v.getVisitDate().isAfter(sixMonthsAgo))
                .filter(v -> v.getDepartment() != null && v.getDepartment().getName().toLowerCase().contains("emergency"))
                .count();
                
        laceScore += Math.min(edVisits, 4); // Max 4 points for ED visits
        
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
