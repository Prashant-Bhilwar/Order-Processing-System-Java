package com.prashant.product.controller;

import com.prashant.product.dto.ProductRequest;
import com.prashant.product.dto.ProductResponse;
import com.prashant.product.mapper.ProductMapper;
import com.prashant.product.security.JwtAuthenticationFilter;
import com.prashant.product.service.JwtService;
import com.prashant.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WithMockUser(username = "admin", roles = {"ADMIN"})
@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductMapper productMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @Test
    void testCreateProduct() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("iPhone 14");
        request.setDescription("Apple flagship");
        request.setPrice(new BigDecimal("999.99"));
        request.setQuantity(10);

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("iPhone 14")
                .description("Apple flagship")
                .price(new BigDecimal("999.99"))
                .quantity(10)
                .build();

        Mockito.when(productService.createProduct(Mockito.any(ProductRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "id": 1L,
                            "name": "iPhone 14",
                            "description": "Apple flagship",
                            "price": 999.99,
                            "quantity": 10
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("iPhone 14"));
    }

    @Test
    void testGetAllProducts() throws Exception {
        List<ProductResponse> products = List.of(
                new ProductResponse(1L, "iPhone", "desc", new BigDecimal("999.99"), 10)
        );

        Mockito.when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("iPhone"));
    }

    @Test
    void testGetProductById() throws Exception {
        ProductResponse response = new ProductResponse(1L, "iPhone", "desc", new BigDecimal("999.99"), 10);

        Mockito.when(productService.getProductById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("iPhone"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        Mockito.doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }
}