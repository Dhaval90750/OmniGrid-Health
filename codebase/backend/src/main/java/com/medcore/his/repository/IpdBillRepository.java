package com.medcore.his.repository;

import com.medcore.his.domain.billing.IpdBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IpdBillRepository extends JpaRepository<IpdBill, UUID> {
    List<IpdBill> findByStatus(String status);
}
