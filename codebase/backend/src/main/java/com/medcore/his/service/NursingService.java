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

    @Autowired
    public NursingService(PatientVitalRepository patientVitalRepository, MedAdministrationRepository medAdministrationRepository) {
        this.patientVitalRepository = patientVitalRepository;
        this.medAdministrationRepository = medAdministrationRepository;
    }

    public List<PatientVital> getVitalsByPatient(UUID patientId) {
        return patientVitalRepository.findByPatientIdOrderByRecordedAtDesc(patientId);
    }

    @Transactional
    public PatientVital saveVitals(PatientVital vital) {
        // Here we could calculate NEWS score based on parameters
        return patientVitalRepository.save(vital);
    }

    public List<MedAdministration> getMarForPatient(UUID patientId) {
        return medAdministrationRepository.findByPatientIdOrderByScheduledTimeAsc(patientId);
    }

    @Transactional
    public MedAdministration recordAdministration(MedAdministration administration) {
        return medAdministrationRepository.save(administration);
    }
}
