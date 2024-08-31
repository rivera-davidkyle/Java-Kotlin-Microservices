package com.example.microservice2.config

import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Exchange
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    @Bean
    fun exchange(): Exchange {
        return TopicExchange("exchange")
    }

    @Bean
    fun bindingKotlinMessageQueue(kotlinMessageQueue: Queue, exchange: Exchange): Binding {
        return BindingBuilder.bind(kotlinMessageQueue).to(exchange).with("kotlinMessageQueue").noargs()
    }

    @Bean
    fun bindingSpringMessageQueue(springMessageQueue: Queue, exchange: Exchange): Binding {
        return BindingBuilder.bind(springMessageQueue).to(exchange).with("springMessageQueue").noargs()
    }
}
