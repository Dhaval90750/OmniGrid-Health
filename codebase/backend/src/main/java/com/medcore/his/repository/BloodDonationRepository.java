package com.medcore.his.repository;

import com.medcore.his.domain.auxiliary.BloodDonation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BloodDonationRepository extends JpaRepository<BloodDonation, UUID> {
    List<BloodDonation> findByBloodGroupAndStatus(String bloodGroup, String status);
}
