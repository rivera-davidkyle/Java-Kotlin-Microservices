package com.example.microservice1.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Exchange exchange() {
        return new TopicExchange("exchange"); 
    }

    @Bean
    public Binding bindingKotlinMessageQueue(Queue kotlinMessageQueue, Exchange exchange) {
        return BindingBuilder.bind(kotlinMessageQueue).to(exchange).with("kotlinMessageQueue").noargs();
    }

    @Bean
    public Binding bindingSpringMessageQueue(Queue springMessageQueue, Exchange exchange) {
        return BindingBuilder.bind(springMessageQueue).to(exchange).with("springMessageQueue").noargs();
    }
}
