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

    @Autowired
    public IcuService(IcuChartRepository icuChartRepository, IcuScoreRepository icuScoreRepository) {
        this.icuChartRepository = icuChartRepository;
        this.icuScoreRepository = icuScoreRepository;
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
        }
        return icuScoreRepository.save(score);
    }
}
