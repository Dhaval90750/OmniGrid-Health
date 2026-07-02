package com.medcore.his.controller;

import com.medcore.his.domain.inventory.InventoryItem;
import com.medcore.his.domain.inventory.PurchaseOrder;
import com.medcore.his.domain.inventory.Vendor;
import com.medcore.his.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_HOSPITAL_ADMIN', 'ROLE_INVENTORY_MGR')")
@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/items")
    public ResponseEntity<List<InventoryItem>> getAllItems() {
        return ResponseEntity.ok(inventoryService.getAllItems());
    }

    @PostMapping("/items")
    public ResponseEntity<InventoryItem> createItem(@RequestBody InventoryItem item) {
        return new ResponseEntity<>(inventoryService.createItem(item), HttpStatus.CREATED);
    }

    @GetMapping("/vendors")
    public ResponseEntity<List<Vendor>> getAllVendors() {
        return ResponseEntity.ok(inventoryService.getAllVendors());
    }

    @PostMapping("/vendors")
    public ResponseEntity<Vendor> createVendor(@RequestBody Vendor vendor) {
        return new ResponseEntity<>(inventoryService.createVendor(vendor), HttpStatus.CREATED);
    }

    @GetMapping("/purchase-orders")
    public ResponseEntity<List<PurchaseOrder>> getAllPurchaseOrders() {
        return ResponseEntity.ok(inventoryService.getAllPurchaseOrders());
    }

    @PostMapping("/purchase-orders")
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(@RequestBody PurchaseOrder po) {
        return new ResponseEntity<>(inventoryService.createPurchaseOrder(po), HttpStatus.CREATED);
    }
    
    @GetMapping("/indents")
    public ResponseEntity<List<com.medcore.his.domain.inventory.PurchaseIndent>> getAllIndents() {
        return ResponseEntity.ok(inventoryService.getAllPurchaseIndents());
    }
    
    @PostMapping("/indents")
    public ResponseEntity<com.medcore.his.domain.inventory.PurchaseIndent> createIndent(@RequestBody com.medcore.his.domain.inventory.PurchaseIndent indent) {
        return new ResponseEntity<>(inventoryService.createPurchaseIndent(indent), HttpStatus.CREATED);
    }
    
    @PostMapping("/indents/{id}/approve")
    public ResponseEntity<com.medcore.his.domain.inventory.PurchaseIndent> approveIndent(@PathVariable java.util.UUID id, @RequestParam String level) {
        return ResponseEntity.ok(inventoryService.approveIndent(id, level));
    }
    
    @GetMapping("/grns")
    public ResponseEntity<List<com.medcore.his.domain.inventory.GoodsReceiptNote>> getAllGrns() {
        return ResponseEntity.ok(inventoryService.getAllGrns());
    }
    
    @PostMapping("/grns")
    public ResponseEntity<com.medcore.his.domain.inventory.GoodsReceiptNote> createGrn(@RequestBody com.medcore.his.domain.inventory.GoodsReceiptNote grn) {
        return new ResponseEntity<>(inventoryService.createGrn(grn), HttpStatus.CREATED);
    }
}
