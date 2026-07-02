package com.medcore.his.controller;

import com.medcore.his.domain.billing.Tariff;
import com.medcore.his.repository.TariffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/tariffs")
public class TariffController {

    private final TariffRepository tariffRepository;

    @Autowired
    public TariffController(TariffRepository tariffRepository) {
        this.tariffRepository = tariffRepository;
    }

    @PostMapping
    public ResponseEntity<Tariff> createTariff(@RequestBody Tariff tariff) {
        return new ResponseEntity<>(tariffRepository.save(tariff), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Tariff>> getAllTariffs() {
        return ResponseEntity.ok(tariffRepository.findAll());
    }
}
