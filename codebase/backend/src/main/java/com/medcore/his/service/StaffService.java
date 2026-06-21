package com.medcore.his.service;

import com.medcore.his.domain.staff.CrossConsultation;
import com.medcore.his.domain.staff.DutyRoster;
import com.medcore.his.domain.staff.LeaveRequest;
import com.medcore.his.domain.staff.StaffProfile;
import com.medcore.his.repository.CrossConsultationRepository;
import com.medcore.his.repository.DutyRosterRepository;
import com.medcore.his.repository.LeaveRequestRepository;
import com.medcore.his.repository.StaffProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StaffService {

    private final StaffProfileRepository staffProfileRepository;
    private final DutyRosterRepository dutyRosterRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final CrossConsultationRepository crossConsultationRepository;

    @Autowired
    public StaffService(StaffProfileRepository staffProfileRepository, DutyRosterRepository dutyRosterRepository, LeaveRequestRepository leaveRequestRepository, CrossConsultationRepository crossConsultationRepository) {
        this.staffProfileRepository = staffProfileRepository;
        this.dutyRosterRepository = dutyRosterRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.crossConsultationRepository = crossConsultationRepository;
    }

    public List<StaffProfile> getAllStaff() {
        return staffProfileRepository.findAll();
    }

    @Transactional
    public StaffProfile createStaff(StaffProfile staff) {
        return staffProfileRepository.save(staff);
    }

    public List<DutyRoster> getAllRosters() {
        return dutyRosterRepository.findAll();
    }

    @Transactional
    public DutyRoster createRoster(DutyRoster roster) {
        return dutyRosterRepository.save(roster);
    }

    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    @Transactional
    public LeaveRequest createLeaveRequest(LeaveRequest request) {
        return leaveRequestRepository.save(request);
    }

    public List<CrossConsultation> getAllConsultations() {
        return crossConsultationRepository.findAll();
    }

    @Transactional
    public CrossConsultation createConsultation(CrossConsultation consultation) {
        return crossConsultationRepository.save(consultation);
    }
}
