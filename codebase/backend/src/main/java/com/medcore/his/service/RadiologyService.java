package com.medcore.his.service;

import com.medcore.his.domain.radiology.RadiologyOrder;
import com.medcore.his.domain.radiology.RadiologyReport;
import com.medcore.his.domain.radiology.RadiologyTemplate;
import com.medcore.his.repository.RadiologyOrderRepository;
import com.medcore.his.repository.RadiologyTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RadiologyService {

    private final RadiologyOrderRepository radiologyOrderRepository;
    private final RadiologyTemplateRepository radiologyTemplateRepository;

    @Autowired
    public RadiologyService(RadiologyOrderRepository radiologyOrderRepository, RadiologyTemplateRepository radiologyTemplateRepository) {
        this.radiologyOrderRepository = radiologyOrderRepository;
        this.radiologyTemplateRepository = radiologyTemplateRepository;
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
        // Tie report to order and update order status
        report.getOrder().setStatus(report.getStatus().equals("Final") ? "Reported" : "Completed");
        // Logic to persist report via a RadiologyReportRepository would go here
        return report;
    }
}
