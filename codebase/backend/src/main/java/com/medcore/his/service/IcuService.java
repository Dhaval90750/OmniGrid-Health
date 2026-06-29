package com.medcore.his.service;

import com.medcore.his.domain.icu.IcuChart;
import com.medcore.his.domain.icu.IcuScore;
import com.medcore.his.repository.IcuChartRepository;
import com.medcore.his.repository.IcuScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class IcuService {

    private final IcuChartRepository icuChartRepository;
    private final IcuScoreRepository icuScoreRepository;
    private final com.medcore.his.repository.PatientVitalRepository patientVitalRepository;

    @Autowired
    public IcuService(IcuChartRepository icuChartRepository, IcuScoreRepository icuScoreRepository, com.medcore.his.repository.PatientVitalRepository patientVitalRepository) {
        this.icuChartRepository = icuChartRepository;
        this.icuScoreRepository = icuScoreRepository;
        this.patientVitalRepository = patientVitalRepository;
    }

    public List<IcuChart> getChartsByPatient(UUID patientId) {
        return icuChartRepository.findByPatientIdOrderByRecordedAtDesc(patientId);
    }

    @Transactional
    public IcuChart saveChart(IcuChart chart) {
        return icuChartRepository.save(chart);
    }

    public List<IcuScore> getScoresByPatient(UUID patientId) {
        return icuScoreRepository.findByPatientIdOrderByRecordedAtDesc(patientId);
    }

    @Transactional
    public IcuScore saveScore(IcuScore score) {
        if ("GCS".equals(score.getScoreType())) {
            int total = (score.getGcsEye() != null ? score.getGcsEye() : 0) +
                        (score.getGcsVerbal() != null ? score.getGcsVerbal() : 0) +
                        (score.getGcsMotor() != null ? score.getGcsMotor() : 0);
            score.setTotalScore(total);
        } else if ("APACHE_II".equals(score.getScoreType())) {
            score.setTotalScore(calculateApacheScore(score));
        }
        return icuScoreRepository.save(score);
    }

    private int calculateApacheScore(IcuScore score) {
        int baseScore = 0;
        
        List<com.medcore.his.domain.nursing.PatientVital> vitals = patientVitalRepository.findByPatientIdOrderByRecordedAtDesc(score.getPatient().getId());
        if (vitals != null && !vitals.isEmpty()) {
            com.medcore.his.domain.nursing.PatientVital latest = vitals.get(0);
            
            // Heuristic scoring based on vitals
            if (latest.getTemperature() != null) {
                double temp = latest.getTemperature().doubleValue();
                if (temp >= 41 || temp <= 29.9) baseScore += 4;
                else if (temp >= 39 || temp <= 31.9) baseScore += 3;
                else if (temp <= 33.9) baseScore += 2;
                else if (temp >= 38.5 || temp <= 35.9) baseScore += 1;
            }
            if (latest.getHeartRate() != null) {
                int hr = latest.getHeartRate();
                if (hr >= 180 || hr <= 39) baseScore += 4;
                else if (hr >= 140 || hr <= 54) baseScore += 3;
                else if (hr >= 110 || hr <= 69) baseScore += 2;
            }
            if (latest.getRespiratoryRate() != null) {
                int rr = latest.getRespiratoryRate();
                if (rr >= 50 || rr <= 5) baseScore += 4;
                else if (rr >= 35) baseScore += 3;
                else if (rr <= 9) baseScore += 2;
                else if (rr >= 25 || rr <= 11) baseScore += 1;
            }
        }
        
        // Add GCS inversion (15 - GCS)
        int gcs = 15;
        if (score.getGcsEye() != null && score.getGcsVerbal() != null && score.getGcsMotor() != null) {
            gcs = score.getGcsEye() + score.getGcsVerbal() + score.getGcsMotor();
        }
        baseScore += (15 - gcs);
        
        return baseScore;
    }
}
