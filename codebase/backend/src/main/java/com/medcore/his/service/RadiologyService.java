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
    private final BillingService billingService;

    @Autowired
    public RadiologyService(RadiologyOrderRepository radiologyOrderRepository, 
                            RadiologyTemplateRepository radiologyTemplateRepository,
                            RadiologyReportRepository radiologyReportRepository,
                            BillingService billingService) {
        this.radiologyOrderRepository = radiologyOrderRepository;
        this.radiologyTemplateRepository = radiologyTemplateRepository;
        this.radiologyReportRepository = radiologyReportRepository;
        this.billingService = billingService;
    }

    public List<RadiologyTemplate> getTemplatesByModality(String modality) {
        return radiologyTemplateRepository.findByModalityIgnoreCase(modality);
    }

    @Transactional(readOnly = true)
    public List<RadiologyOrder> getPendingOrdersByModality(String modality) {
        return radiologyOrderRepository.findByModalityIgnoreCaseAndStatusOrderByCreatedAtAsc(modality, "Scheduled");
    }

    @Transactional
    public RadiologyOrder placeOrder(RadiologyOrder order) {
        order.setStatus("Scheduled");
        
        if (order.getPatient() != null) {
            billingService.addChargeToPatient(
                order.getPatient().getId(),
                "Radiology: " + order.getModality() + " - " + order.getStudyDescription(),
                java.math.BigDecimal.valueOf(1500.0), // Standard Radiology price
                1
            );
        }
        
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

    @Transactional(readOnly = true)
    public List<RadiologyReport> getCriticalReports() {
        return radiologyReportRepository.findAll().stream()
                .filter(r -> r.getIsCritical() != null && r.getIsCritical())
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RadiologyReport> getReportsByPatient(java.util.UUID patientId) {
        return radiologyReportRepository.findAll().stream()
                .filter(r -> r.getOrder().getPatient().getId().equals(patientId))
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public java.util.Map<String, Long> getRadiologyStats() {
        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        
        List<RadiologyOrder> allOrders = radiologyOrderRepository.findAll();
        
        stats.put("pending", allOrders.stream().filter(o -> "Scheduled".equals(o.getStatus())).count());
        stats.put("completed", allOrders.stream().filter(o -> "Completed".equals(o.getStatus())).count());
        stats.put("reported", allOrders.stream().filter(o -> "Reported".equals(o.getStatus())).count());
        
        long tatBreached = allOrders.stream().filter(o -> {
            if ("Reported".equals(o.getStatus())) return false; // If already reported, don't count in pending breach (unless we look at historical breaches, but for now we look at pending breaches).
            
            long maxHours = 24; // Routine
            if ("Urgent".equalsIgnoreCase(o.getUrgency())) maxHours = 12;
            if ("Stat".equalsIgnoreCase(o.getUrgency())) maxHours = 4;
            
            return o.getCreatedAt().isBefore(java.time.LocalDateTime.now().minusHours(maxHours));
        }).count();
        
        stats.put("tatBreached", tatBreached);
        
        return stats;
    }
}
