package com.medcore.his.repository;

import com.medcore.his.domain.radiology.RadiologyTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RadiologyTemplateRepository extends JpaRepository<RadiologyTemplate, UUID> {
    List<RadiologyTemplate> findByModalityIgnoreCase(String modality);
}
