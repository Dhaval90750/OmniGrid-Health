package com.medcore.his.repository;

import com.medcore.his.domain.discharge.Discharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DischargeRepository extends JpaRepository<Discharge, UUID> {
}
