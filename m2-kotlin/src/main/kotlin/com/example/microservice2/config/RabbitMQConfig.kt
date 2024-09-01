package com.example.microservice2.config

import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    @Bean
    fun exchange(): DirectExchange {
        return DirectExchange("exchange")
    }

    @Bean
    fun bindingKotlinMessageQueue(kotlinMessageQueue: Queue, exchange: DirectExchange): Binding {
        return BindingBuilder.bind(kotlinMessageQueue).to(exchange).with("kotlinMessageQueue")
    }

    @Bean
    fun bindingSpringMessageQueue(springMessageQueue: Queue, exchange: DirectExchange): Binding {
        return BindingBuilder.bind(springMessageQueue).to(exchange).with("springMessageQueue")
    }
}
