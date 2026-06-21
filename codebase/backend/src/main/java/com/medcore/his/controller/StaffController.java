package com.medcore.his.controller;

import com.medcore.his.domain.staff.CrossConsultation;
import com.medcore.his.domain.staff.DutyRoster;
import com.medcore.his.domain.staff.LeaveRequest;
import com.medcore.his.domain.staff.StaffProfile;
import com.medcore.his.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/staff")
public class StaffController {

    private final StaffService staffService;

    @Autowired
    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("/profiles")
    public ResponseEntity<List<StaffProfile>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @PostMapping("/profiles")
    public ResponseEntity<StaffProfile> createStaff(@RequestBody StaffProfile staff) {
        return new ResponseEntity<>(staffService.createStaff(staff), HttpStatus.CREATED);
    }

    @GetMapping("/rosters")
    public ResponseEntity<List<DutyRoster>> getAllRosters() {
        return ResponseEntity.ok(staffService.getAllRosters());
    }

    @PostMapping("/rosters")
    public ResponseEntity<DutyRoster> createRoster(@RequestBody DutyRoster roster) {
        return new ResponseEntity<>(staffService.createRoster(roster), HttpStatus.CREATED);
    }

    @GetMapping("/leaves")
    public ResponseEntity<List<LeaveRequest>> getAllLeaves() {
        return ResponseEntity.ok(staffService.getAllLeaveRequests());
    }

    @PostMapping("/leaves")
    public ResponseEntity<LeaveRequest> createLeave(@RequestBody LeaveRequest request) {
        return new ResponseEntity<>(staffService.createLeaveRequest(request), HttpStatus.CREATED);
    }

    @GetMapping("/consults")
    public ResponseEntity<List<CrossConsultation>> getAllConsults() {
        return ResponseEntity.ok(staffService.getAllConsultations());
    }

    @PostMapping("/consults")
    public ResponseEntity<CrossConsultation> createConsult(@RequestBody CrossConsultation consult) {
        return new ResponseEntity<>(staffService.createConsultation(consult), HttpStatus.CREATED);
    }
}
