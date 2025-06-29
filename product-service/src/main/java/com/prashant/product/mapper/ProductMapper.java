package com.prashant.product.mapper;

import com.prashant.product.dto.ProductResponse;
import com.prashant.product.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse mapToResponse(Product product);
}
