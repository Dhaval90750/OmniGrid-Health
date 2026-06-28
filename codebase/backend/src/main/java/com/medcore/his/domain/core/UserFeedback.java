package com.medcore.his.domain.core;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_feedback")
@Getter
@Setter
public class UserFeedback extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_id")
    private User reportedBy;

    @Column(nullable = false, length = 100)
    private String category; // e.g. BUG, FEATURE_REQUEST, USABILITY

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comments;

    @Column(nullable = false)
    private int rating; // 1 to 5 scale

    @Column(length = 50)
    private String status = "OPEN"; // OPEN, REVIEWED, IN_PROGRESS, CLOSED
}
