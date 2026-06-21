package com.medcore.his.service;

import com.medcore.his.domain.billing.InsuranceClaim;
import com.medcore.his.domain.billing.IpdBill;
import com.medcore.his.repository.InsuranceClaimRepository;
import com.medcore.his.repository.IpdBillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IpdBillingService {

    private final IpdBillRepository ipdBillRepository;
    private final InsuranceClaimRepository insuranceClaimRepository;

    @Autowired
    public IpdBillingService(IpdBillRepository ipdBillRepository, InsuranceClaimRepository insuranceClaimRepository) {
        this.ipdBillRepository = ipdBillRepository;
        this.insuranceClaimRepository = insuranceClaimRepository;
    }

    public List<IpdBill> getAllIpdBills() {
        return ipdBillRepository.findAll();
    }

    @Transactional
    public IpdBill generateIpdBill(IpdBill bill) {
        bill.setStatus("Draft");
        return ipdBillRepository.save(bill);
    }

    public List<InsuranceClaim> getAllClaims() {
        return insuranceClaimRepository.findAll();
    }

    @Transactional
    public InsuranceClaim createClaim(InsuranceClaim claim) {
        claim.setStatus("PreAuth_Pending");
        return insuranceClaimRepository.save(claim);
    }
}
