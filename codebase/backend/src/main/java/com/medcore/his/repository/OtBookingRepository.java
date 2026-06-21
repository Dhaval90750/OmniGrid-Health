package com.medcore.his.repository;

import com.medcore.his.domain.ot.OtBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OtBookingRepository extends JpaRepository<OtBooking, UUID> {
    List<OtBooking> findAllByOrderByScheduledTimeAsc();
}
