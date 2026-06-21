package com.medcore.his.repository;

import com.medcore.his.domain.pharmacy.PharmacyStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PharmacyStockRepository extends JpaRepository<PharmacyStock, UUID> {
    List<PharmacyStock> findByDrugIdOrderByExpiryDateAsc(UUID drugId);
}
