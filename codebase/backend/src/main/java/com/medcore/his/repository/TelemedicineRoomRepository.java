package com.medcore.his.repository;

import com.medcore.his.domain.clinical.TelemedicineRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TelemedicineRoomRepository extends JpaRepository<TelemedicineRoom, UUID> {
    Optional<TelemedicineRoom> findByRoomId(String roomId);
}
