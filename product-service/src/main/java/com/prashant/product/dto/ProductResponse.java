package com.prashant.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Product details response")
public class ProductResponse {

    @Schema(description = "Product ID", example = "1")
    private Long id;

    @Schema(description = "Product name", example = "iPhone 14 Pro")
    private String name;

    @Schema(description = "Description", example = "Latest Apple smartphone")
    private String description;

    @Schema(description = "Price", example = "999.99")
    private BigDecimal price;

    @Schema(description = "Available quantity", example = "100")
    private Integer quantity;
}
