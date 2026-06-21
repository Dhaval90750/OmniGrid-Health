package com.medcore.his.repository;

import com.medcore.his.domain.radiology.RadiologyOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RadiologyOrderRepository extends JpaRepository<RadiologyOrder, UUID> {
}
