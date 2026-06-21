package com.medcore.his.repository;

import com.medcore.his.domain.operations.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID> {
}
