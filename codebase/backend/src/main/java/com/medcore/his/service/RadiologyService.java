package com.medcore.his.service;

import com.medcore.his.domain.radiology.RadiologyOrder;
import com.medcore.his.domain.radiology.RadiologyReport;
import com.medcore.his.domain.radiology.RadiologyTemplate;
import com.medcore.his.repository.RadiologyOrderRepository;
import com.medcore.his.repository.RadiologyReportRepository;
import com.medcore.his.repository.RadiologyTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RadiologyService {

    private final RadiologyOrderRepository radiologyOrderRepository;
    private final RadiologyTemplateRepository radiologyTemplateRepository;
    private final RadiologyReportRepository radiologyReportRepository;

    @Autowired
    public RadiologyService(RadiologyOrderRepository radiologyOrderRepository, 
                            RadiologyTemplateRepository radiologyTemplateRepository,
                            RadiologyReportRepository radiologyReportRepository) {
        this.radiologyOrderRepository = radiologyOrderRepository;
        this.radiologyTemplateRepository = radiologyTemplateRepository;
        this.radiologyReportRepository = radiologyReportRepository;
    }

    public List<RadiologyTemplate> getTemplatesByModality(String modality) {
        return radiologyTemplateRepository.findByModalityIgnoreCase(modality);
    }

    @Transactional
    public RadiologyOrder placeOrder(RadiologyOrder order) {
        order.setStatus("Scheduled");
        return radiologyOrderRepository.save(order);
    }

    @Transactional
    public RadiologyReport saveReport(RadiologyReport report) {
        RadiologyOrder order = radiologyOrderRepository.findById(report.getOrder().getId())
                .orElseThrow(() -> new RuntimeException("Radiology Order not found"));
        
        report.setOrder(order);
        if ("Final".equals(report.getStatus())) {
            order.setStatus("Reported");
            report.setFinalizedAt(java.time.LocalDateTime.now());
        } else {
            order.setStatus("Completed");
        }
        
        radiologyOrderRepository.save(order);
        return radiologyReportRepository.save(report);
    }
}
