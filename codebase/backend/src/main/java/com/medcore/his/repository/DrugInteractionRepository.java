package com.medcore.his.repository;

import com.medcore.his.domain.pharmacy.DrugInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DrugInteractionRepository extends JpaRepository<DrugInteraction, UUID> {
    List<DrugInteraction> findByPrimaryDrugGenericIgnoreCaseAndSecondaryDrugGenericIgnoreCase(String primary, String secondary);
}
