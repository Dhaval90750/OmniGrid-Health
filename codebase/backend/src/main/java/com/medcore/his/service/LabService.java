package com.medcore.his.service;

import com.medcore.his.domain.lab.LabOrder;
import com.medcore.his.domain.lab.LabResult;
import com.medcore.his.domain.lab.LabSample;
import com.medcore.his.domain.lab.LabTest;
import com.medcore.his.repository.LabOrderRepository;
import com.medcore.his.repository.LabTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LabService {

    private final LabOrderRepository labOrderRepository;
    private final LabTestRepository labTestRepository;
    private final com.medcore.his.repository.LabSampleRepository labSampleRepository;
    private final com.medcore.his.repository.LabResultRepository labResultRepository;
    private final BillingService billingService;

    @Autowired
    public LabService(LabOrderRepository labOrderRepository, LabTestRepository labTestRepository,
                      com.medcore.his.repository.LabSampleRepository labSampleRepository,
                      com.medcore.his.repository.LabResultRepository labResultRepository,
                      BillingService billingService) {
        this.labOrderRepository = labOrderRepository;
        this.labTestRepository = labTestRepository;
        this.labSampleRepository = labSampleRepository;
        this.labResultRepository = labResultRepository;
        this.billingService = billingService;
    }

    public List<LabTest> searchTests(String query) {
        return labTestRepository.findByTestNameContainingIgnoreCaseOrTestCodeContainingIgnoreCase(query, query);
    }

    @Transactional(readOnly = true)
    public List<LabResult> getResultsByPatient(UUID patientId) {
        return labResultRepository.findAll().stream()
                .filter(r -> r.getSample().getOrder().getPatient().getId().equals(patientId))
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LabSample> getSamplesByStatus(String status) {
        return labSampleRepository.findByStatus(status);
    }

    @Transactional
    public LabOrder placeOrder(LabOrder order) {
        for (LabSample sample : order.getSamples()) {
            sample.setOrder(order);
            sample.setStatus("Pending_Collection");
            
            // Charge the patient for each requested test
            if (sample.getTest() != null && order.getPatient() != null) {
                billingService.addChargeToPatient(
                    order.getPatient().getId(),
                    "Lab Test: " + sample.getTest().getTestName(),
                    java.math.BigDecimal.valueOf(50.0),
                    1
                );
            }
        }
        order.setStatus("Ordered");
        return labOrderRepository.save(order);
    }

    @Transactional
    public List<LabSample> generateSampleBarcodes(UUID orderId) {
        LabOrder order = labOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Lab Order not found"));
        
        for (LabSample sample : order.getSamples()) {
            if (sample.getBarcode() == null || sample.getBarcode().isEmpty()) {
                String barcode = "LAB-" + LocalDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                sample.setBarcode(barcode);
                sample.setStatus("Pending_Collection");
            }
        }
        labOrderRepository.save(order);
        return order.getSamples();
    }

    @Transactional
    public LabSample collectSample(UUID sampleId, com.medcore.his.domain.auth.User collector) {
        LabSample sample = labSampleRepository.findById(sampleId)
                .orElseThrow(() -> new RuntimeException("Sample not found"));
        sample.setStatus("Collected");
        sample.setCollectedAt(LocalDateTime.now());
        sample.setCollectedBy(collector);
        return labSampleRepository.save(sample);
    }

    @Transactional
    public void authorizeResults(UUID sampleId, List<LabResult> results, com.medcore.his.domain.auth.User authorizer) {
        LabSample sample = labSampleRepository.findById(sampleId)
                .orElseThrow(() -> new RuntimeException("Sample not found"));
        
        for (LabResult result : results) {
            result.setSample(sample);
            result.setAuthorizedBy(authorizer);
            result.setAuthorizedAt(LocalDateTime.now());
            labResultRepository.save(result);
        }
        
        sample.setStatus("Completed");
        labSampleRepository.save(sample);
        
        // Check if all samples in order are completed
        LabOrder order = sample.getOrder();
        boolean allCompleted = order.getSamples().stream()
                .allMatch(s -> "Completed".equals(s.getStatus()));
                
        if (allCompleted) {
            order.setStatus("Completed");
            labOrderRepository.save(order);
        }
    }

    @Transactional
    public LabSample receiveSample(UUID sampleId, boolean accept, String reason) {
        LabSample sample = labSampleRepository.findById(sampleId)
                .orElseThrow(() -> new RuntimeException("Sample not found"));
                
        if (accept) {
            sample.setStatus("Received");
            sample.setReceivedAt(LocalDateTime.now());
        } else {
            sample.setStatus("Rejected");
            sample.setRejectionReason(reason);
        }
        return labSampleRepository.save(sample);
    }

    @Transactional
    public List<LabResult> enterResults(UUID sampleId, List<LabResult> results, com.medcore.his.domain.auth.User user) {
        LabSample sample = labSampleRepository.findById(sampleId)
                .orElseThrow(() -> new RuntimeException("Sample not found"));
        
        LabTest test = sample.getTest();
        
        // Find previous result for delta check
        LabResult previousResult = labResultRepository.findAll().stream()
                .filter(r -> r.getSample().getOrder().getPatient().getId().equals(sample.getOrder().getPatient().getId()))
                .filter(r -> r.getSample().getTest().getId().equals(test.getId()))
                .filter(r -> r.getSample().getId() != sampleId)
                .max((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt()))
                .orElse(null);

        for (LabResult result : results) {
            result.setSample(sample);
            // DO NOT set authorizedBy and authorizedAt here. They belong in authorizeResults.

            // Flagging Engine
            if (test.getReferenceRangeLow() != null && test.getReferenceRangeHigh() != null && result.getResultValue() != null) {
                if (result.getResultValue().compareTo(test.getReferenceRangeLow()) < 0 || 
                    result.getResultValue().compareTo(test.getReferenceRangeHigh()) > 0) {
                    result.setIsAbnormal(true);
                    
                    // Critical threshold (e.g., 20% outside range)
                    java.math.BigDecimal range = test.getReferenceRangeHigh().subtract(test.getReferenceRangeLow());
                    java.math.BigDecimal criticalMargin = range.multiply(new java.math.BigDecimal("0.2"));
                    
                    if (result.getResultValue().compareTo(test.getReferenceRangeLow().subtract(criticalMargin)) < 0 ||
                        result.getResultValue().compareTo(test.getReferenceRangeHigh().add(criticalMargin)) > 0) {
                        result.setIsCritical(true);
                    }
                }
            }
            
            // Delta Check Engine
            if (previousResult != null && previousResult.getResultValue() != null && result.getResultValue() != null) {
                java.math.BigDecimal diff = result.getResultValue().subtract(previousResult.getResultValue()).abs();
                java.math.BigDecimal prevVal = previousResult.getResultValue().equals(java.math.BigDecimal.ZERO) ? java.math.BigDecimal.ONE : previousResult.getResultValue();
                double percentChange = diff.divide(prevVal, 4, java.math.RoundingMode.HALF_UP).doubleValue() * 100;
                
                if (percentChange > 25.0) { // Flag if changed by more than 25%
                    result.setDeltaWarning(String.format("Changed %.1f%% since last result", percentChange));
                }
            }

            labResultRepository.save(result);
        }
        
        sample.setStatus("Result_Entered");
        labSampleRepository.save(sample);
        
        // We only mark order completed when samples are "Completed" or "Rejected" (handled in authorizeResults)
        return results;
    }
}
