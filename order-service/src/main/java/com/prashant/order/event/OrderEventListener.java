package com.prashant.order.event;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        System.out.println("Order Event Received");
        System.out.println("    Order ID    : " + event.getOrderId());
        System.out.println("    User ID     : " + event.getUserId());
        System.out.println("    Product ID  : " + event.getProductId());
        System.out.println("    Quantity    : " + event.getQuantity());
        System.out.println("    Amount      : " + event.getTotalAmount());
        System.out.println("    Created At  : " + event.getCreatedAt());
    }
}
