package com.medcore.his.repository;

import com.medcore.his.domain.billing.InsurancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, UUID> {
}
