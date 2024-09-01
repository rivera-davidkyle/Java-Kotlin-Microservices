package com.example.microservice1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MicroserviceApplicationTests {

    @Autowired
    private MicroserviceApplication microserviceApplication;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void testSendMessage() {
        String queueName = "kotlinMessageQueue";
        String message = "ping";

        microserviceApplication.sendMessage(queueName, message);

        ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> queueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(rabbitTemplate, times(1)).convertAndSend(exchangeCaptor.capture(), queueCaptor.capture(), messageCaptor.capture());

        assertEquals("exchange", exchangeCaptor.getValue());
        assertEquals(queueName, queueCaptor.getValue());
        assertEquals(message, messageCaptor.getValue());
    }

    @Test
    void testReceiveMessage() {
        String queueName = "springMessageQueue";
        String expectedMessage = "pong";
        when(rabbitTemplate.receiveAndConvert(queueName)).thenReturn(expectedMessage);

        String actualMessage = microserviceApplication.receiveMessage(queueName);

        assertEquals(expectedMessage, actualMessage);
        verify(rabbitTemplate, times(1)).receiveAndConvert(queueName);
    }
}
