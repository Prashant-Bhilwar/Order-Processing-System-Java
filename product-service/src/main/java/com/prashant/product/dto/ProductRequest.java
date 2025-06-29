package com.prashant.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank
    @Schema(description = "Name of the product", example = "iPhone 14 Pro")
    private String name;

    @Schema(description = "Product description", example = "Latest Apple smartphone")
    private String description;

    @NotNull
    @Positive
    @Schema(description = "Product price", example = "999.99")
    private BigDecimal price;

    @NotNull
    @Min(0)
    @Schema(description = "Available quantity", example = "100")
    private Integer quantity;
}
