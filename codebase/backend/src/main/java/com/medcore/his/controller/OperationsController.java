package com.medcore.his.controller;

import com.medcore.his.domain.operations.HousekeepingTask;
import com.medcore.his.domain.operations.TransportRequest;
import com.medcore.his.domain.operations.WorkOrder;
import com.medcore.his.service.OperationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
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
}
