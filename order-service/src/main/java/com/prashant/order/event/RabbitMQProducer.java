package com.prashant.order.event;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.management.Query;

@Component
@RequiredArgsConstructor
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;
    private final AmqpAdmin amqpAdmin;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    @PostConstruct
    public void setup() {
        TopicExchange topicExchange = new TopicExchange(exchange);
        Queue queue = new Queue("order.queue", true);
        Binding binding = BindingBuilder.bind(queue).to(topicExchange).with(routingKey);

        amqpAdmin.declareExchange(topicExchange);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(binding);
    }

    public void publish(OrderCreatedEvent event){
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }

}
