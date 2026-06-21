package com.medcore.his.service;

import com.medcore.his.domain.ot.OtBooking;
import com.medcore.his.domain.ot.SurgeryRecord;
import com.medcore.his.repository.OtBookingRepository;
import com.medcore.his.repository.SurgeryRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OtService {

    private final OtBookingRepository otBookingRepository;
    private final SurgeryRecordRepository surgeryRecordRepository;

    @Autowired
    public OtService(OtBookingRepository otBookingRepository, SurgeryRecordRepository surgeryRecordRepository) {
        this.otBookingRepository = otBookingRepository;
        this.surgeryRecordRepository = surgeryRecordRepository;
    }

    public List<OtBooking> getAllBookings() {
        return otBookingRepository.findAllByOrderByScheduledTimeAsc();
    }

    @Transactional
    public OtBooking createBooking(OtBooking booking) {
        return otBookingRepository.save(booking);
    }

    @Transactional
    public SurgeryRecord saveSurgeryRecord(SurgeryRecord record) {
        if (record.getCompletedAt() != null) {
            record.getBooking().setStatus("Completed");
        } else {
            record.getBooking().setStatus("In_Progress");
        }
        return surgeryRecordRepository.save(record);
    }
}
