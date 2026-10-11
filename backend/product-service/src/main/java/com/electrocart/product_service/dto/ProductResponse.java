package com.electrocart.product_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse (
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String category,
        String brand,
        String imageUrl,
        LocalDateTime createdAt
){
}
