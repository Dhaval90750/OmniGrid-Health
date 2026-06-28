package com.medcore.his.controller;

import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.core.UserFeedback;
import com.medcore.his.repository.UserFeedbackRepository;
import com.medcore.his.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    private final UserFeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    @Autowired
    public FeedbackController(UserFeedbackRepository feedbackRepository, UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> submitFeedback(@RequestBody Map<String, Object> payload) {
        String userIdStr = (String) payload.get("userId");
        String category = (String) payload.get("category");
        String comments = (String) payload.get("comments");
        Integer rating = (Integer) payload.get("rating");

        if (comments == null || category == null || rating == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields (category, comments, rating)"));
        }

        UserFeedback feedback = new UserFeedback();
        feedback.setCategory(category);
        feedback.setComments(comments);
        feedback.setRating(rating);

        if (userIdStr != null) {
            try {
                Optional<User> userOpt = userRepository.findById(UUID.fromString(userIdStr));
                userOpt.ifPresent(feedback::setReportedBy);
            } catch (IllegalArgumentException ignored) {
            }
        }

        feedbackRepository.save(feedback);
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Feedback submitted successfully."));
    }

    @GetMapping
    public ResponseEntity<List<UserFeedback>> getOpenFeedback() {
        return ResponseEntity.ok(feedbackRepository.findByStatusOrderByCreatedAtDesc("OPEN"));
    }
}
