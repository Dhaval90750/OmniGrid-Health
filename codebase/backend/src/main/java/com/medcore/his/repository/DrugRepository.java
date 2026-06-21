package com.medcore.his.repository;

import com.medcore.his.domain.clinical.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DrugRepository extends JpaRepository<Drug, UUID> {
    List<Drug> findByGenericNameContainingIgnoreCaseOrBrandNameContainingIgnoreCase(String genericName, String brandName);
}
