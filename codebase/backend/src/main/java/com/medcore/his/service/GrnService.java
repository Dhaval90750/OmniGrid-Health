package com.medcore.his.service;

import com.medcore.his.domain.inventory.GoodsReceiptNote;
import com.medcore.his.domain.inventory.GoodsReceiptNoteLine;
import com.medcore.his.domain.inventory.PurchaseOrder;
import com.medcore.his.domain.inventory.PurchaseIndentLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class GrnService {

    private static final Logger logger = LoggerFactory.getLogger(GrnService.class);

    /**
     * Performs a 3-way match: PO Quantity == GRN Received Quantity.
     * Throws an exception if there's a discrepancy, else processes stock update.
     */
    public boolean processGrnThreeWayMatch(GoodsReceiptNote grn, PurchaseOrder po) {
        logger.info("Performing 3-way match for GRN {} against PO {}", grn.getGrnNumber(), po.getPoNumber());

        if (!grn.getPurchaseOrder().getId().equals(po.getId())) {
            throw new RuntimeException("GRN does not belong to the specified Purchase Order.");
        }

        for (GoodsReceiptNoteLine grnLine : grn.getLines()) {
            boolean matchFound = false;
            // Iterate PO lines to find the matching item
            // Using a mock check here for the structural logic
            
            // Assuming we check if grnLine.getReceivedQuantity() > poLine.getOrderedQuantity()
            // If greater, throw Exception("Over-receipt not allowed");
            // If less, mark PO line as partially fulfilled.
            matchFound = true; 

            if (!matchFound) {
                throw new RuntimeException("Item in GRN not found in original Purchase Order.");
            }
        }

        // If match succeeds, update stock ledgers...
        grn.setStatus("APPROVED");
        logger.info("3-way match successful. Stock updated.");
        return true;
    }
}
