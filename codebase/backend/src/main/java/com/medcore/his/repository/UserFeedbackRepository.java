package com.medcore.his.repository;

import com.medcore.his.domain.core.UserFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserFeedbackRepository extends JpaRepository<UserFeedback, UUID> {
    List<UserFeedback> findByStatusOrderByCreatedAtDesc(String status);
}
