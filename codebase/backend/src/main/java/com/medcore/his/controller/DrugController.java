package com.medcore.his.controller;

import com.medcore.his.domain.clinical.Drug;
import com.medcore.his.repository.DrugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/drugs")
public class DrugController {

    private final DrugRepository drugRepository;

    @Autowired
    public DrugController(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Drug>> searchDrugs(@RequestParam String q) {
        List<Drug> results = drugRepository.findByGenericNameContainingIgnoreCaseOrBrandNameContainingIgnoreCase(q, q);
        return ResponseEntity.ok(results);
    }
}
