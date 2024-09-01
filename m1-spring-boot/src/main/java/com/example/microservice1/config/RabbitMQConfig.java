package com.example.microservice1.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Binding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("exchange"); 
    }

    @Bean
    public Binding bindingKotlinMessageQueue(Queue kotlinMessageQueue, DirectExchange exchange) {
        return BindingBuilder.bind(kotlinMessageQueue).to(exchange).with("kotlinMessageQueue");
    }

    @Bean
    public Binding bindingSpringMessageQueue(Queue springMessageQueue, DirectExchange exchange) {
        return BindingBuilder.bind(springMessageQueue).to(exchange).with("springMessageQueue");
    }
}
