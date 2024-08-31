package com.example.microservice2

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import javax.annotation.PostConstruct
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SpringBootApplication
class MicroserviceApplication(private val rabbitTemplate: RabbitTemplate) {

    @Bean
    fun kotlinMessageQueue(): Queue = Queue("kotlinMessageQueue", false)

    @Bean
    fun springMessageQueue(): Queue = Queue("springMessageQueue", false)

    fun logMessage(message: String) {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedNow = now.format(formatter)
        
        println("[$formattedNow] $message")
    }

    @PostConstruct
    fun startMessaging() {
        GlobalScope.launch {
            while (true) {
                // Receive message from S1
                val message = rabbitTemplate.receiveAndConvert("kotlinMessageQueue") as String?
                if (message != null) {
                    logMessage("S2 received: $message")
                    if (message == "ping") {
                        // Send message to S1
                        rabbitTemplate.convertAndSend("springMessageQueue", "pong")
                        logMessage("S2 sent: pong")
                        delay(10000) // 10 seconds delay
                        rabbitTemplate.convertAndSend("springMessageQueue", "ping")
                        logMessage("S2 sent: ping")
                    }
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<MicroserviceApplication>(*args)
}
