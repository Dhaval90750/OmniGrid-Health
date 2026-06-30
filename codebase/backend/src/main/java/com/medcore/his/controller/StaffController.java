package com.medcore.his.controller;

import com.medcore.his.domain.staff.CrossConsultation;
import com.medcore.his.domain.staff.DutyRoster;
import com.medcore.his.domain.staff.LeaveRequest;
import com.medcore.his.domain.staff.StaffProfile;
import com.medcore.his.domain.clinical.Visit;
import com.medcore.his.service.StaffService;
import com.medcore.his.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/staff")
public class StaffController {

    private final StaffService staffService;
    private final VisitRepository visitRepository;
    private final com.medcore.his.repository.StaffProfileRepository staffProfileRepository;
    private final com.medcore.his.repository.InternLogbookRepository internLogbookRepository;

    @Autowired
    public StaffController(StaffService staffService, VisitRepository visitRepository, com.medcore.his.repository.StaffProfileRepository staffProfileRepository, com.medcore.his.repository.InternLogbookRepository internLogbookRepository) {
        this.staffService = staffService;
        this.visitRepository = visitRepository;
        this.staffProfileRepository = staffProfileRepository;
        this.internLogbookRepository = internLogbookRepository;
    }

    @GetMapping("/profiles")
    public ResponseEntity<List<StaffProfile>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @PostMapping("/profiles")
    public ResponseEntity<StaffProfile> createStaff(@RequestBody StaffProfile staff) {
        return new ResponseEntity<>(staffService.createStaff(staff), HttpStatus.CREATED);
    }

    @PutMapping("/profiles/{id}")
    public ResponseEntity<StaffProfile> updateStaff(@PathVariable java.util.UUID id, @RequestBody StaffProfile updatedStaff) {
        return ResponseEntity.ok(staffService.updateStaff(id, updatedStaff));
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<StaffProfile>> getDoctors() {
        return ResponseEntity.ok(staffProfileRepository.findByRoleIn(List.of("Consultant", "Resident")));
    }

    @GetMapping("/doctors/{id}/queue")
    public ResponseEntity<List<Visit>> getDoctorQueue(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(visitRepository.findActiveQueueByDoctorAndDate(id, java.time.LocalDateTime.now()));
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
    
    @PostMapping("/leaves/{id}/approve")
    public ResponseEntity<LeaveRequest> approveLeave(@PathVariable java.util.UUID id, @RequestParam java.util.UUID approverId, @RequestParam boolean approve) {
        return ResponseEntity.ok(staffService.approveLeaveRequest(id, approverId, approve));
    }

    @GetMapping("/consults")
    public ResponseEntity<List<CrossConsultation>> getAllConsults() {
        return ResponseEntity.ok(staffService.getAllConsultations());
    }

    @PostMapping("/consults")
    public ResponseEntity<CrossConsultation> createConsult(@RequestBody CrossConsultation consult) {
        return new ResponseEntity<>(staffService.createConsultation(consult), HttpStatus.CREATED);
    }

    @PostMapping("/logbooks")
    public ResponseEntity<com.medcore.his.domain.staff.InternLogbook> createLogbook(@RequestBody com.medcore.his.domain.staff.InternLogbook logbook) {
        return new ResponseEntity<>(internLogbookRepository.save(logbook), HttpStatus.CREATED);
    }
}
