package com.medcore.his.repository;

import com.medcore.his.domain.master.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BedRepository extends JpaRepository<Bed, UUID> {
}
