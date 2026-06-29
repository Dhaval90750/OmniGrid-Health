package com.medcore.his.service;

import com.medcore.his.domain.core.SystemConfiguration;
import com.medcore.his.repository.SystemConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConfigurationService {

    private final SystemConfigurationRepository repository;

    @Autowired
    public ConfigurationService(SystemConfigurationRepository repository) {
        this.repository = repository;
    }

    public String getValue(String key, String defaultValue) {
        return repository.findByConfigKey(key)
                .map(SystemConfiguration::getConfigValue)
                .orElse(defaultValue);
    }

    public Map<String, String> getAllConfigurations() {
        Map<String, String> map = new HashMap<>();
        repository.findAll().forEach(config -> map.put(config.getConfigKey(), config.getConfigValue()));
        return map;
    }

    public void updateConfigurations(Map<String, String> configs) {
        configs.forEach((key, value) -> {
            SystemConfiguration config = repository.findByConfigKey(key).orElseGet(() -> {
                SystemConfiguration newConfig = new SystemConfiguration();
                newConfig.setConfigKey(key);
                return newConfig;
            });
            config.setConfigValue(value);
            repository.save(config);
        });
    }
}
