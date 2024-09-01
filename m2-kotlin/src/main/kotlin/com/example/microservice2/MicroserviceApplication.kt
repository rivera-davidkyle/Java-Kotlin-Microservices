package com.example.microservice2

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import javax.annotation.PostConstruct
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SpringBootApplication
class MicroserviceApplication(private val rabbitTemplate: RabbitTemplate, private val environment: Environment) {

    /**
     * Defines a RabbitMQ queue for microservice 1
     * 
     * @return A Queue instance for "springMessageQueue".
     */
    @Bean
    fun springMessageQueue(): Queue = Queue("springMessageQueue", false)
    
    /**
     * Defines a RabbitMQ queue for microservice 2
     * 
     * @return A Queue instance for "kotlinMessageQueue".
     */
    @Bean
    fun kotlinMessageQueue(): Queue = Queue("kotlinMessageQueue", false)

    /**
     * Logs a message to the console with a timestamp.
     * 
     * @param message The message to log.
     */
    fun logMessage(message: String) {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedNow = now.format(formatter)
        
        println("[$formattedNow] $message")
    }

    /**
     * Sends a message to the specified RabbitMQ queue.
     * 
     * @param queueName The name of the queue to send the message to.
     * @param message The message to send.
     */
    fun sendMessage(queueName: String, message: String) {
        rabbitTemplate.convertAndSend(queueName, message)
    }

    /**
     * Receives a message from the specified RabbitMQ queue.
     * 
     * @param queueName The name of the queue to receive the message from.
     * @return The received message, or null if no message is available.
     */
    fun receiveMessage(queueName: String): String? {
        return rabbitTemplate.receiveAndConvert(queueName) as String?
    }

    /**
     * Checks if the current Spring profile is set to "test".
     * 
     * @return {@code true} if the "test" profile is active; {@code false} otherwise.
     */
    private fun isTestEnvironment(): Boolean {
        return environment.activeProfiles.contains("test")
    }

    /**
     * Starts the messaging process once the application is initialized.
     * Continuously listens for messages from the "kotlinMessageQueue",
     * and based on the received messages, sends "pong" or "ping" messages with a delay.
     */
    @PostConstruct
    fun startMessaging() {
        if (isTestEnvironment()) {
            println("Skipping startMessaging in test environment.")
            return
        }
        
        GlobalScope.launch {
            while (true) {
                // Receive message from S1
                val message = receiveMessage("kotlinMessageQueue")
                if (message != null) {
                    logMessage("S2 received: $message")
                    if (message == "ping") {
                        // Send message to S1
                        sendMessage("springMessageQueue", "pong")
                        logMessage("S2 sent: pong")
                        delay(10000) // 10 seconds delay
                        sendMessage("springMessageQueue", "ping")
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
