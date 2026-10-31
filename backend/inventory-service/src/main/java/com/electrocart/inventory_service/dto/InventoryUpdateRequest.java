package com.electrocart.inventory_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryUpdateRequest {

    @NotNull(message = "Available Quantity is required")
    @Min(value = 0, message = "Available Quantity cannot be negative")
    private Integer availableQuantity;
}
