package com.electrocart.inventory_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class InventoryCreateRequest {

    @NotNull(message = "Product id is required")
    private UUID productId;

    @NotNull(message = "Initial Quantity is required")
    @Min(value = 0,message = "Initial Quantity must be positive")
    private Integer initialQuantity;
}
