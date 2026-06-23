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
        return workOrderRepository.save(order);
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
