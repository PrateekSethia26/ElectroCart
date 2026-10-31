package com.electrocart.inventory_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryResponse(
        Long id,
        UUID productId,
        int availableQuantity,
        int reservedQuantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
