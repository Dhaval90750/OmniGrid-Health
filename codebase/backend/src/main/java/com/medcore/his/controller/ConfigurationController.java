package com.medcore.his.controller;

import com.medcore.his.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/admin/settings")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @Autowired
    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getAllSettings() {
        return ResponseEntity.ok(configurationService.getAllConfigurations());
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> updateSettings(@RequestBody Map<String, String> settings) {
        configurationService.updateConfigurations(settings);
        return ResponseEntity.ok(configurationService.getAllConfigurations());
    }
}
