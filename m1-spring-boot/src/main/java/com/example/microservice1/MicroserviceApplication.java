package com.example.microservice1;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.env.Environment;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@SpringBootApplication
public class MicroserviceApplication {

    private final RabbitTemplate rabbitTemplate;

    private final Environment environment;

    /**
     * Constructor for the MicroserviceApplication class.
     * 
     * @param rabbitTemplate The RabbitTemplate used for messaging.
     */
    @Autowired
    public MicroserviceApplication(RabbitTemplate rabbitTemplate, Environment environment) {
        this.rabbitTemplate = rabbitTemplate;
        this.environment = environment;
    }
    
    /**
     * Defines a RabbitMQ queue for microservice 1
     * 
     * @return A Queue instance for "springMessageQueue".
     */
    @Bean
    public Queue springMessageQueue() {
        return new Queue("springMessageQueue", false);
    }
    
    /**
     * Defines a RabbitMQ queue for microservice 2
     * 
     * @return A Queue instance for "kotlinMessageQueue".
     */
    @Bean
    public Queue kotlinMessageQueue() {
        return new Queue("kotlinMessageQueue", false);
    }

    /**
     * 
     * This method examines the active profiles of the Spring application context
     * and determines if "test" is included in the list of active profiles. It is
     * used to conditionally execute code only in specific environments.
     * 
     * @return {@code true} if the "test" profile is active; {@code false} otherwise.
     */
    private boolean isTestEnvironment() {
        return environment.getActiveProfiles() != null
                && Arrays.asList(environment.getActiveProfiles()).contains("test");
    }
    
    /**
     * Logs a message to the console with a timestamp.
     * 
     * @param message The message to log.
     */
    void logMessage(String message) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = now.format(formatter);

        System.out.println("[" + formattedNow + "] " + message);
    }

    /**
     * Sends a message to the specified RabbitMQ queue.
     * 
     * @param queueName The name of the queue to send the message to.
     * @param message The message to send.
     */
    void sendMessage(String queueName, String message) {
        rabbitTemplate.convertAndSend("exchange", queueName, message);
    }

    /**
     * Receives a message from the specified RabbitMQ queue.
     * 
     * @param queueName The name of the queue to receive the message from.
     * @return The received message, or null if no message is available.
     */
    String receiveMessage(String queueName) {
        return (String) rabbitTemplate.receiveAndConvert(queueName);
    }

    /**
     * 
     * Starts the messaging process once the application is ready.
     * Sends an initial "ping" message to the Kotlin queue and continuously
     * listens for messages from the Spring queue.
     * 
     * @throws InterruptedException If the thread is interrupted while sleeping.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startMessaging() throws InterruptedException {
        if (isTestEnvironment()) {
            System.out.println("Skipping startMessaging in test environment.");
            return;
        }

        // Send initial message to S2
        sendMessage("kotlinMessageQueue", "ping");
        logMessage("S1 sent: ping");
        while (true) {
            // Receive message from S2
            String message = receiveMessage("springMessageQueue");
            if (message != null) {
                logMessage("S1 received: " + message);
                if (message.equals("ping")) {
                    // Send message to S2
                    sendMessage("kotlinMessageQueue", "pong");
                    logMessage("S1 sent: pong");
                    Thread.sleep(10000); // 10 seconds delay
                    sendMessage("kotlinMessageQueue", "ping");
                    logMessage("S1 sent: ping");
                }
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceApplication.class, args);
    }
}
