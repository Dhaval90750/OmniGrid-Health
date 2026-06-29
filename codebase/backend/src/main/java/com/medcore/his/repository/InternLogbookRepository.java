package com.medcore.his.repository;

import com.medcore.his.domain.staff.InternLogbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InternLogbookRepository extends JpaRepository<InternLogbook, UUID> {
}
