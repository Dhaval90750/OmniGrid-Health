package com.medcore.his.repository;

import com.medcore.his.domain.inventory.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, UUID> {
}
