package com.medcore.his.controller;

import com.medcore.his.domain.inventory.InventoryItem;
import com.medcore.his.domain.inventory.PurchaseOrder;
import com.medcore.his.domain.inventory.Vendor;
import com.medcore.his.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
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
}
