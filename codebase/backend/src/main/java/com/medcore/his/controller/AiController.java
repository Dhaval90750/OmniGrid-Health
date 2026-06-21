package com.medcore.his.controller;

import com.medcore.his.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiService aiService;

    @Autowired
    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/extract")
    public ResponseEntity<Map<String, Object>> extractEntities(@RequestBody Map<String, String> request) {
        String transcript = request.getOrDefault("transcript", "");
        return ResponseEntity.ok(aiService.extractMedicalEntities(transcript));
    }
}
