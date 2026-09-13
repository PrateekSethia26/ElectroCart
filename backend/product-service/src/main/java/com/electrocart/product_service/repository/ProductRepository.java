package com.electrocart.product_service.repository;

import com.electrocart.product_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>,
        JpaSpecificationExecutor<Product> {

    Page<Product> findByNameContainingIgnoreCase(String Name,Pageable pageable);

    Page<Product> findByCategoryIgnoreCase(String category,Pageable pageable);

    Page<Product> findByBrandIgnoreCase(String brand,Pageable pageable);
}
