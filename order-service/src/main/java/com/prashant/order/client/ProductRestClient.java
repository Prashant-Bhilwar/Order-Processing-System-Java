package com.prashant.order.client;

import com.prashant.order.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ProductRestClient {

    private final WebClient webClient;
    public ProductDto getProductById(Long productId) {
        return webClient.get()
                .uri("http://localhost:8082/api/products/internal/{id}", productId)
                .retrieve()
                .bodyToMono(ProductDto.class)
                .block();
    }
}
