package com.medcore.his.controller;

import com.medcore.his.domain.operations.HousekeepingTask;
import com.medcore.his.domain.operations.TransportRequest;
import com.medcore.his.domain.operations.WorkOrder;
import com.medcore.his.service.OperationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_OPERATIONS_MGR')")
@RestController
@RequestMapping("/api/v1/operations")
public class OperationsController {

    private final OperationsService operationsService;

    @Autowired
    public OperationsController(OperationsService operationsService) {
        this.operationsService = operationsService;
    }

    @GetMapping("/housekeeping")
    public ResponseEntity<List<HousekeepingTask>> getAllHousekeepingTasks() {
        return ResponseEntity.ok(operationsService.getAllHousekeepingTasks());
    }

    @PostMapping("/housekeeping")
    public ResponseEntity<HousekeepingTask> createHousekeepingTask(@RequestBody HousekeepingTask task) {
        return new ResponseEntity<>(operationsService.createHousekeepingTask(task), HttpStatus.CREATED);
    }

    @GetMapping("/work-orders")
    public ResponseEntity<List<WorkOrder>> getAllWorkOrders() {
        return ResponseEntity.ok(operationsService.getAllWorkOrders());
    }

    @PostMapping("/work-orders")
    public ResponseEntity<WorkOrder> createWorkOrder(@RequestBody WorkOrder order) {
        return new ResponseEntity<>(operationsService.createWorkOrder(order), HttpStatus.CREATED);
    }

    @GetMapping("/transport")
    public ResponseEntity<List<TransportRequest>> getAllTransportRequests() {
        return ResponseEntity.ok(operationsService.getAllTransportRequests());
    }

    @PostMapping("/transport")
    public ResponseEntity<TransportRequest> createTransportRequest(@RequestBody TransportRequest request) {
        return new ResponseEntity<>(operationsService.createTransportRequest(request), HttpStatus.CREATED);
    }

    @GetMapping("/incidents")
    public ResponseEntity<List<com.medcore.his.domain.operations.IncidentReport>> getAllIncidentReports() {
        return ResponseEntity.ok(operationsService.getAllIncidentReports());
    }

    @PostMapping("/incidents")
    public ResponseEntity<com.medcore.his.domain.operations.IncidentReport> createIncidentReport(@RequestBody com.medcore.his.domain.operations.IncidentReport report) {
        return new ResponseEntity<>(operationsService.createIncidentReport(report), HttpStatus.CREATED);
    }
}
