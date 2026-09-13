package com.electrocart.product_service.service;

import com.electrocart.product_service.dto.ProductRequest;
import com.electrocart.product_service.dto.ProductResponse;
import com.electrocart.product_service.entity.Product;
import com.electrocart.product_service.exception.ResourceNotFoundException;
import com.electrocart.product_service.repository.ProductRepository;
import com.electrocart.product_service.specification.ProductSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest request){
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .category(request.category())
                .brand(request.brand())
                .imageUrl(request.imageUrl())
                .build();

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    public ProductResponse getProductById(UUID id){
        Product product = productRepository.findById(id)
                .orElseThrow(()->
                        new ResourceNotFoundException("Product not found with id: " + id)
                );
        return mapToResponse(product);
    }

    public Page<ProductResponse> getAllProducts(
            String search,
            String category,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable){

        Specification<Product> specification =
                Specification.allOf(
                        ProductSpecification.hasSearch(search),
                        ProductSpecification.hasCategory(category),
                        ProductSpecification.hasBrand(brand),
                        ProductSpecification.priceGreaterThanOrEqualTo(minPrice),
                        ProductSpecification.priceLessThanOrEqualTo(maxPrice)
                );

        return productRepository
                .findAll(specification,pageable)
                .map(this::mapToResponse);
    }

    public void deleteProduct(UUID id){
        if(!productRepository.existsById(id)){
            throw new ResourceNotFoundException("Product not found with id "+id);
        }

        productRepository.deleteById(id);
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getBrand(),
                product.getImageUrl(),
                product.getCreatedAt()
        );
    }

    public ProductResponse updateProduct(UUID id, @Valid ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException(
                        "Product not found with id "+id
                ));
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(request.category());
        product.setBrand(request.brand());
        product.setImageUrl(request.imageUrl());

        Product updatedProduct = productRepository.save(product);

        return mapToResponse(updatedProduct);
    }
}
