package com.prashant.order.client;

import com.prashant.order.dto.ProductDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductClient {

    public ProductDto getProductById(Long productId) {
        return new ProductDto(productId, "mock Product", new BigDecimal("100.0"));
    }
}
