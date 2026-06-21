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

import java.util.List;

@Service
public class LabService {

    private final LabOrderRepository labOrderRepository;
    private final LabTestRepository labTestRepository;

    @Autowired
    public LabService(LabOrderRepository labOrderRepository, LabTestRepository labTestRepository) {
        this.labOrderRepository = labOrderRepository;
        this.labTestRepository = labTestRepository;
    }

    public List<LabTest> searchTests(String query) {
        return labTestRepository.findByTestNameContainingIgnoreCaseOrTestCodeContainingIgnoreCase(query, query);
    }

    @Transactional
    public LabOrder placeOrder(LabOrder order) {
        for (LabSample sample : order.getSamples()) {
            sample.setOrder(order);
            sample.setStatus("Pending_Collection");
        }
        order.setStatus("Ordered");
        return labOrderRepository.save(order);
    }

    // A real system would have separate methods for collecting, receiving, and entering results
    // For MVP we can just save it all at once or mock the state changes in the controller.
}
