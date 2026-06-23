package com.medcore.his.service;

import com.medcore.his.domain.nursing.MedAdministration;
import com.medcore.his.domain.nursing.PatientVital;
import com.medcore.his.repository.MedAdministrationRepository;
import com.medcore.his.repository.PatientVitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NursingService {

    private final PatientVitalRepository patientVitalRepository;
    private final MedAdministrationRepository medAdministrationRepository;
    private final com.medcore.his.repository.ShiftHandoverRepository shiftHandoverRepository;

    @Autowired
    public NursingService(PatientVitalRepository patientVitalRepository, 
                          MedAdministrationRepository medAdministrationRepository,
                          com.medcore.his.repository.ShiftHandoverRepository shiftHandoverRepository) {
        this.patientVitalRepository = patientVitalRepository;
        this.medAdministrationRepository = medAdministrationRepository;
        this.shiftHandoverRepository = shiftHandoverRepository;
    }

    public List<PatientVital> getVitalsByPatient(UUID patientId) {
        return patientVitalRepository.findByPatientIdOrderByRecordedAtDesc(patientId);
    }

    @Transactional
    public PatientVital saveVitals(PatientVital vital) {
        vital.setNewsScore(calculateNewsScore(vital));
        return patientVitalRepository.save(vital);
    }

    private Integer calculateNewsScore(PatientVital v) {
        int score = 0;
        // Respiratory Rate
        if (v.getRespiratoryRate() != null) {
            int rr = v.getRespiratoryRate();
            if (rr <= 8 || rr >= 25) score += 3;
            else if (rr >= 21 || rr <= 11) score += 1;
        }
        // SpO2
        if (v.getSpo2() != null) {
            int spo2 = v.getSpo2();
            if (spo2 <= 91) score += 3;
            else if (spo2 <= 93) score += 2;
            else if (spo2 <= 95) score += 1;
        }
        // Systolic BP
        if (v.getBloodPressureSystolic() != null) {
            int sbp = v.getBloodPressureSystolic();
            if (sbp <= 90 || sbp >= 220) score += 3;
            else if (sbp <= 100) score += 2;
            else if (sbp <= 110) score += 1;
        }
        // Heart Rate
        if (v.getHeartRate() != null) {
            int hr = v.getHeartRate();
            if (hr <= 40 || hr >= 131) score += 3;
            else if (hr >= 111) score += 2;
            else if (hr <= 50 || hr >= 91) score += 1;
        }
        // Temperature
        if (v.getTemperature() != null) {
            double temp = v.getTemperature().doubleValue();
            if (temp <= 35.0) score += 3;
            else if (temp >= 39.1) score += 2;
            else if (temp <= 36.0 || temp >= 38.1) score += 1;
        }
        return score;
    }

    public List<MedAdministration> getMarForPatient(UUID patientId) {
        return medAdministrationRepository.findByPatientIdOrderByScheduledTimeAsc(patientId);
    }

    @Transactional
    public MedAdministration recordAdministration(MedAdministration administration) {
        return medAdministrationRepository.save(administration);
    }

    @Transactional
    public com.medcore.his.domain.nursing.ShiftHandover saveShiftHandover(com.medcore.his.domain.nursing.ShiftHandover handover) {
        return shiftHandoverRepository.save(handover);
    }

    public List<com.medcore.his.domain.nursing.ShiftHandover> getHandoversForPatient(UUID patientId) {
        return shiftHandoverRepository.findByPatientIdOrderByShiftDateTimeDesc(patientId);
    }
}
