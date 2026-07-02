package com.medcore.his.controller;

import com.medcore.his.domain.master.Ward;
import com.medcore.his.repository.WardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/wards")
public class WardController {

    private final WardRepository wardRepository;

    @Autowired
    public WardController(WardRepository wardRepository) {
        this.wardRepository = wardRepository;
    }

    @PostMapping
    public ResponseEntity<Ward> createWard(@RequestBody Ward ward) {
        return new ResponseEntity<>(wardRepository.save(ward), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Ward>> getAllWards() {
        return ResponseEntity.ok(wardRepository.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ward> updateWard(@PathVariable java.util.UUID id, @RequestBody Ward updatedWard) {
        return wardRepository.findById(id)
            .map(ward -> {
                ward.setCode(updatedWard.getCode());
                ward.setName(updatedWard.getName());
                ward.setCategory(updatedWard.getCategory());
                ward.setActive(updatedWard.isActive());
                return ResponseEntity.ok(wardRepository.save(ward));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
