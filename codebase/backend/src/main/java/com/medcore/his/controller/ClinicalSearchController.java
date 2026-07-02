package com.medcore.his.controller;

import com.medcore.his.domain.search.DrugIndex;
import com.medcore.his.domain.search.Icd10Index;
import com.medcore.his.repository.search.DrugSearchRepository;
import com.medcore.his.repository.search.Icd10SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/search")
public class ClinicalSearchController {

    private final Icd10SearchRepository icd10SearchRepository;
    private final DrugSearchRepository drugSearchRepository;

    @Autowired
    public ClinicalSearchController(Icd10SearchRepository icd10SearchRepository, DrugSearchRepository drugSearchRepository) {
        this.icd10SearchRepository = icd10SearchRepository;
        this.drugSearchRepository = drugSearchRepository;
    }

    @GetMapping("/icd10")
    public ResponseEntity<List<Icd10Index>> searchIcd10(@RequestParam String q) {
        return ResponseEntity.ok(icd10SearchRepository.findByDescriptionContainingIgnoreCaseOrCodeContainingIgnoreCase(q, q));
    }

    @GetMapping("/drugs")
    public ResponseEntity<List<DrugIndex>> searchDrugs(@RequestParam String q) {
        return ResponseEntity.ok(drugSearchRepository.findByGenericNameContainingIgnoreCaseOrBrandNameContainingIgnoreCase(q, q));
    }
}
