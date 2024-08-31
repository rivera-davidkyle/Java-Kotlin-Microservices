package com.example.microservice1;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class MicroserviceApplication {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Bean
    public Queue kotlinMessageQueue() {
        return new Queue("kotlinMessageQueue", false);
    }

    @Bean
    public Queue springMessageQueue() {
        return new Queue("springMessageQueue", false);
    }

    // Function to log a message with a timestamp
    public static void logMessage(String message) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = now.format(formatter);

        System.out.println("[" + formattedNow + "] " + message);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startMessaging() throws InterruptedException {
        // Send initial message to S2
        rabbitTemplate.convertAndSend("exchange", "kotlinMessageQueue", "ping");
        logMessage("S1 sent: ping");
        while (true) {
            // Receive message from S2
            String message = (String) rabbitTemplate.receiveAndConvert("springMessageQueue");
            if (message != null) {
                logMessage("S1 received: " + message);
                if (message.equals("ping")) {
                    // Send  message to S2
                    rabbitTemplate.convertAndSend("exchange", "kotlinMessageQueue", "pong");
                    logMessage("S1 sent: pong");
                    Thread.sleep(10000); // 10 seconds delay
                    rabbitTemplate.convertAndSend("exchange", "kotlinMessageQueue", "ping");
                    logMessage("S1 sent: ping");
                }
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceApplication.class, args);
    }
}
