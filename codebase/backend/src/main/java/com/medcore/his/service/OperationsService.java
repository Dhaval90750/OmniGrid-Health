package com.medcore.his.service;

import com.medcore.his.domain.operations.HousekeepingTask;
import com.medcore.his.domain.operations.TransportRequest;
import com.medcore.his.domain.operations.WorkOrder;
import com.medcore.his.repository.HousekeepingTaskRepository;
import com.medcore.his.repository.TransportRequestRepository;
import com.medcore.his.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OperationsService {

    private final HousekeepingTaskRepository housekeepingTaskRepository;
    private final WorkOrderRepository workOrderRepository;
    private final TransportRequestRepository transportRequestRepository;
    private final com.medcore.his.repository.IncidentReportRepository incidentReportRepository;

    @Autowired
    public OperationsService(HousekeepingTaskRepository housekeepingTaskRepository, 
                             WorkOrderRepository workOrderRepository, 
                             TransportRequestRepository transportRequestRepository,
                             com.medcore.his.repository.IncidentReportRepository incidentReportRepository) {
        this.housekeepingTaskRepository = housekeepingTaskRepository;
        this.workOrderRepository = workOrderRepository;
        this.transportRequestRepository = transportRequestRepository;
        this.incidentReportRepository = incidentReportRepository;
    }

    public List<HousekeepingTask> getAllHousekeepingTasks() {
        return housekeepingTaskRepository.findAll();
    }

    @Transactional
    public HousekeepingTask createHousekeepingTask(HousekeepingTask task) {
        return housekeepingTaskRepository.save(task);
    }

    public List<WorkOrder> getAllWorkOrders() {
        return workOrderRepository.findAll();
    }

    @Transactional
    public WorkOrder createWorkOrder(WorkOrder order) {
        // Calculate SLA based on priority
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        switch (order.getPriority()) {
            case "Critical":
                order.setSlaDeadline(now.plusHours(2));
                break;
            case "High":
                order.setSlaDeadline(now.plusHours(8));
                break;
            case "Low":
                order.setSlaDeadline(now.plusDays(3));
                break;
            default: // Medium
                order.setSlaDeadline(now.plusHours(24));
                break;
        }
        return workOrderRepository.save(order);
    }
    
    // Scheduled job to check SLA breaches
    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000) // Every minute
    @Transactional
    public void checkSlaBreaches() {
        List<WorkOrder> openOrders = workOrderRepository.findAll();
        for (WorkOrder order : openOrders) {
            if (!order.isBreached() && !order.getStatus().equals("Resolved") && !order.getStatus().equals("Closed")) {
                if (order.getSlaDeadline() != null && java.time.LocalDateTime.now().isAfter(order.getSlaDeadline())) {
                    order.setBreached(true);
                    workOrderRepository.save(order);
                }
            }
        }
    }

    public List<TransportRequest> getAllTransportRequests() {
        return transportRequestRepository.findAll();
    }

    @Transactional
    public TransportRequest createTransportRequest(TransportRequest request) {
        return transportRequestRepository.save(request);
    }

    public List<com.medcore.his.domain.operations.IncidentReport> getAllIncidentReports() {
        return incidentReportRepository.findAll();
    }

    @Transactional
    public com.medcore.his.domain.operations.IncidentReport createIncidentReport(com.medcore.his.domain.operations.IncidentReport report) {
        return incidentReportRepository.save(report);
    }
}
