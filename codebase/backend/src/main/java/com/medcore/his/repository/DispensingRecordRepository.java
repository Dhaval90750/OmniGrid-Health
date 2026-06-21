package com.medcore.his.repository;

import com.medcore.his.domain.pharmacy.DispensingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DispensingRecordRepository extends JpaRepository<DispensingRecord, UUID> {
}
