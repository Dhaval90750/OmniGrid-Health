package com.medcore.his.repository;

import com.medcore.his.domain.inventory.PurchaseIndent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseIndentRepository extends JpaRepository<PurchaseIndent, UUID> {
    List<PurchaseIndent> findByStatusOrderByCreatedAtDesc(String status);
}
