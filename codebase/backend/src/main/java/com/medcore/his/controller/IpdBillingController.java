package com.medcore.his.controller;

import com.medcore.his.domain.billing.InsuranceClaim;
import com.medcore.his.domain.billing.IpdBill;
import com.medcore.his.service.IpdBillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/ipd-billing")
public class IpdBillingController {

    private final IpdBillingService ipdBillingService;

    @Autowired
    public IpdBillingController(IpdBillingService ipdBillingService) {
        this.ipdBillingService = ipdBillingService;
    }

    @GetMapping("/bills")
    public ResponseEntity<List<IpdBill>> getAllBills() {
        return ResponseEntity.ok(ipdBillingService.getAllIpdBills());
    }

    @PostMapping("/bills")
    public ResponseEntity<IpdBill> generateBill(@RequestBody IpdBill bill) {
        return new ResponseEntity<>(ipdBillingService.generateIpdBill(bill), HttpStatus.CREATED);
    }

    @GetMapping("/claims")
    public ResponseEntity<List<InsuranceClaim>> getAllClaims() {
        return ResponseEntity.ok(ipdBillingService.getAllClaims());
    }

    @PostMapping("/claims")
    public ResponseEntity<InsuranceClaim> createClaim(@RequestBody InsuranceClaim claim) {
        return new ResponseEntity<>(ipdBillingService.createClaim(claim), HttpStatus.CREATED);
    }
}
