package com.electrocart.inventory_service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class ProductServiceClient {

    private final RestClient restClient;

    public ProductServiceClient(
            @Qualifier("loadBalancedRestClientBuilder")
            RestClient.Builder builder) {

        this.restClient = builder
                .baseUrl("http://PRODUCT-SERVICE")
                .build();
    }

    public void validateProductExists(UUID productId){
        try {
            restClient.get()
                    .uri("/api/products/{id}",productId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception){

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product not found: "+productId
            );
        }
    }

}
