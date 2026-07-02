package com.medcore.his.repository;

import com.medcore.his.domain.clinical.Icd10;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface Icd10Repository extends JpaRepository<Icd10, UUID> {
    List<Icd10> findByCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String code, String description);
    
    @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM icd10_codes WHERE text_search @@ plainto_tsquery('english', :query) OR code ILIKE CONCAT(:query, '%') LIMIT 20", nativeQuery = true)
    List<Icd10> searchByText(@org.springframework.data.repository.query.Param("query") String query);
}
