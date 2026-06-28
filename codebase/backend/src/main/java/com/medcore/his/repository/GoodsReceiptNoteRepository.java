package com.medcore.his.repository;

import com.medcore.his.domain.inventory.GoodsReceiptNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GoodsReceiptNoteRepository extends JpaRepository<GoodsReceiptNote, UUID> {
    List<GoodsReceiptNote> findByPurchaseOrderId(UUID poId);
}
