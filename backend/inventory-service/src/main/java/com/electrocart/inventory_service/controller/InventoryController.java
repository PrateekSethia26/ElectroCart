package com.electrocart.inventory_service.controller;

import com.electrocart.inventory_service.dto.InventoryCreateRequest;
import com.electrocart.inventory_service.dto.InventoryResponse;
import com.electrocart.inventory_service.dto.InventoryUpdateRequest;
import com.electrocart.inventory_service.dto.StockQuantityRequest;
import com.electrocart.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventory(){
        return ResponseEntity.ok(
                inventoryService.getAllInventory()
        );
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(@PathVariable UUID productId){
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> initializeInventory(
            @RequestBody InventoryCreateRequest request){

        InventoryResponse inventory = inventoryService.initializeInventory(
                request.getProductId(),
                request.getInitialQuantity()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventory);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<InventoryResponse> updateAvailableQuantity(
            @PathVariable UUID productId,
            @RequestBody InventoryUpdateRequest request){

        return ResponseEntity.ok(
                inventoryService.updateInventory(
                        productId,
                        request.getAvailableQuantity()));
    }

    @PostMapping("/{productId}/reserve")
    public ResponseEntity<InventoryResponse> reserveStock(
            @PathVariable UUID productId,
            @Valid @RequestBody StockQuantityRequest request){

        System.out.println("RESERVE CONTROLLER REACHED in Reserve");
        InventoryResponse response = inventoryService.reserveStock(
                productId,
                request.quantity()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{productId}/release")
    public ResponseEntity<InventoryResponse> releaseStock(
            @PathVariable UUID productId,
            @Valid @RequestBody StockQuantityRequest request){

        System.out.println("RESERVE CONTROLLER REACHED in Release");
        InventoryResponse response = inventoryService.releaseStock(
                productId,
                request.quantity()
        );

        return ResponseEntity.ok(response);
    }
}
