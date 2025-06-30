package com.prashant.order.controller;

import com.prashant.order.dto.OrderRequest;
import com.prashant.order.dto.OrderResponse;
import com.prashant.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestBody @Valid OrderRequest request,
            @AuthenticationPrincipal UserDetails user) {
        OrderResponse response = orderService.placeOrder(request, user.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrder(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(orderService.getOrdersByUser(user.getUsername()));
    }
}
