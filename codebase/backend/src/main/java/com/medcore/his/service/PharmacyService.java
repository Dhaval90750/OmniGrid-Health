package com.medcore.his.service;

import com.medcore.his.domain.pharmacy.DispensingRecord;
import com.medcore.his.domain.pharmacy.PharmacyStock;
import com.medcore.his.domain.pharmacy.StockMovement;
import com.medcore.his.repository.DispensingRecordRepository;
import com.medcore.his.repository.PharmacyStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PharmacyService {

    private final PharmacyStockRepository stockRepository;
    private final DispensingRecordRepository dispensingRepository;

    @Autowired
    public PharmacyService(PharmacyStockRepository stockRepository, DispensingRecordRepository dispensingRepository) {
        this.stockRepository = stockRepository;
        this.dispensingRepository = dispensingRepository;
    }

    public List<PharmacyStock> getStockByDrug(UUID drugId) {
        return stockRepository.findByDrugIdOrderByExpiryDateAsc(drugId);
    }

    @Transactional
    public DispensingRecord dispensePrescription(DispensingRecord record, List<StockMovement> deductions) {
        for (StockMovement movement : deductions) {
            PharmacyStock stock = stockRepository.findById(movement.getStock().getId())
                    .orElseThrow(() -> new RuntimeException("Stock not found"));
            
            // Note: movement.getQuantityChange() should be negative for dispensing
            stock.setQuantity(stock.getQuantity() + movement.getQuantityChange());
            stockRepository.save(stock);
            
            movement.setMovementType("Dispense");
            movement.setStock(stock);
            // In a real implementation, we would save the movement to a StockMovementRepository here.
        }
        
        return dispensingRepository.save(record);
    }
}
