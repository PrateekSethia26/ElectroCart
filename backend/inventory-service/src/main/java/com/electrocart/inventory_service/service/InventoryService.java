package com.electrocart.inventory_service.service;

import com.electrocart.inventory_service.client.ProductServiceClient;
import com.electrocart.inventory_service.dto.InventoryResponse;
import com.electrocart.inventory_service.entity.Inventory;
import com.electrocart.inventory_service.exception.InventoryNotFoundException;
import com.electrocart.inventory_service.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductServiceClient productServiceClient;

    public InventoryResponse getInventoryByProductId(UUID productId){
        Inventory inventory =  inventoryRepository.findByProductId(productId)
                .orElseThrow(()->
                        new InventoryNotFoundException(
                                "Inventory not found for product id: " + productId
                        ));

        return mapToResponse(inventory);

    }

    public List<InventoryResponse> getAllInventory(){
        return inventoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public InventoryResponse initializeInventory(
            UUID productId,
            int initialQuantity
    ){
        if(productId == null){
            throw new IllegalArgumentException("Product ID is required");
        }

        if(initialQuantity<0){
            throw new IllegalArgumentException("Initial quantity cannot be negative");
        }

        // Step 1: Verify that the product exists in Product Service.
        productServiceClient.validateProductExists(productId);

        // Step 2: Prevent duplicate inventory initialization.
        if(inventoryRepository.existsByProductId(productId)){
            throw new IllegalStateException(
                    "Inventory already exists for product ID: "
                            + productId
            );
        }

        // Step 3: Create inventory only after successful validation.
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setAvailableQuantity(initialQuantity);
        inventory.setReserveQuantity(0);

        try{
            Inventory savedInventory = inventoryRepository.save(inventory);

            return mapToResponse(savedInventory);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException(
                    "Inventory already exists for product ID: " + productId,
                    ex
            );
        }
    }

    @Transactional
    public InventoryResponse updateInventory(
            UUID productId,
            int newQuantity){

        if(productId == null){
            throw new IllegalArgumentException(
                    "Product ID must not be null"
            );
        }

        if(newQuantity < 0){
            throw new IllegalArgumentException(
                    "Available Quantity cannot be negative"
            );
        }

        Inventory inventory = inventoryRepository.findByProductIdForUpdate(productId)
                .orElseThrow(()->new InventoryNotFoundException(
                        "Inventory not found for product ID: " + productId));

        inventory.setAvailableQuantity(newQuantity);

        Inventory updatedInventory =  inventoryRepository.save(inventory);

        return mapToResponse(updatedInventory);
    }

    @Transactional
    public InventoryResponse reserveStock(UUID productId, int quantity){

        validateStockRequest(productId,quantity);

        Inventory inventory = inventoryRepository
                .findByProductIdForUpdate(productId)
                .orElseThrow(()->
                        new InventoryNotFoundException(
                                "Inventory not found for product ID: " + productId));

        if(inventory.getAvailableQuantity() < quantity){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Insufficient available stock. Available: "
                    +inventory.getAvailableQuantity()
                    +", Requested Quantity: "+quantity
            );
        }

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() - quantity
        );

        inventory.setReserveQuantity(
                inventory.getReserveQuantity() + quantity
        );

        Inventory updatedInventory = inventoryRepository.save(inventory);

        return mapToResponse(updatedInventory);
    }

    @Transactional
    public InventoryResponse releaseStock(UUID productId,int quantity){
        validateStockRequest(productId,quantity);

        Inventory inventory = inventoryRepository
                .findByProductIdForUpdate(productId)
                .orElseThrow(()-> new InventoryNotFoundException(
                        "Inventory not found for product ID: "+productId
                ));

        if(inventory.getReserveQuantity() < quantity){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Insufficient reserve stock. Reserved: "
                    +inventory.getReserveQuantity()
                    +" ,requested: " + quantity
            );
        }

        inventory.setReserveQuantity(
                inventory.getReserveQuantity() - quantity
        );

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() + quantity
        );

        Inventory updatedInventory = inventoryRepository.save(inventory);

        return mapToResponse(updatedInventory);
    }

    private void validateStockRequest(UUID productId, int quantity) {

        if(productId == null){
            throw new IllegalArgumentException(
                    "Product ID not found: "+productId
            );
        }

        if(quantity<=0){
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }
    }

    private InventoryResponse mapToResponse(Inventory inventory){
        return new InventoryResponse(
                inventory.getId(),
                inventory.getProductId(),
                inventory.getAvailableQuantity(),
                inventory.getReserveQuantity(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt()
        );
    }
}
