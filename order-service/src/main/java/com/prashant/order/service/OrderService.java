package com.prashant.order.service;

import com.prashant.order.client.ProductRestClient;
import com.prashant.order.dto.OrderRequest;
import com.prashant.order.dto.OrderResponse;
import com.prashant.order.dto.ProductDto;
import com.prashant.order.entity.Order;
import com.prashant.order.event.OrderCreatedEvent;
import com.prashant.order.event.RabbitMQProducer;
import com.prashant.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRestClient productClient;
    private final RabbitMQProducer rabbitMQProducer;

    public OrderResponse placeOrder(OrderRequest request, String email) {
        ProductDto product = productClient.getProductById(request.getProductId());

        BigDecimal totalAmount =product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        Order order = Order.builder()
                .userId(extractUserId(email))
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .totalAmount(totalAmount)
                .createdAt(LocalDateTime.now())
                .build();

        Order saved = orderRepository.save(order);

        rabbitMQProducer.publish(
                OrderCreatedEvent.builder()
                        .orderId(saved.getId())
                        .userId(saved.getUserId())
                        .productId(saved.getProductId())
                        .quantity(saved.getQuantity())
                        .totalAmount(saved.getTotalAmount())
                        .createdAt(saved.getCreatedAt())
                        .build()
        );

        return mapToResponse(saved);
    }

    public List<OrderResponse> getOrdersByUser(String email) {
        Long userId = extractUserId(email);
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private Long extractUserId(String email) {
        return (long) email.hashCode();
    }
}
