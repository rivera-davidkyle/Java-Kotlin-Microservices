package com.example.microservice2

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking

@ExtendWith(SpringExtension::class)
@SpringBootTest
@ActiveProfiles("test")
class MicroserviceApplicationTests {

    @MockBean
    private lateinit var rabbitTemplate: RabbitTemplate

    private lateinit var microserviceApplication: MicroserviceApplication

    @Test
    fun `test send message`() = runBlocking {
        val queueName = "kotlinMessageQueue"
        val message = "ping"

        microserviceApplication.sendMessage(queueName, message)

        val exchangeCaptor = ArgumentCaptor.forClass(String::class.java)
        val queueCaptor = ArgumentCaptor.forClass(String::class.java)
        val messageCaptor = ArgumentCaptor.forClass(String::class.java)
        verify(rabbitTemplate, times(1)).convertAndSend(exchangeCaptor.capture(), queueCaptor.capture(), messageCaptor.capture())

        assertEquals("exchange", exchangeCaptor.value)
        assertEquals(queueName, queueCaptor.value)
        assertEquals(message, messageCaptor.value)
    }

    @Test
    fun `test receive message`() = runBlocking {
        val queueName = "springMessageQueue"
        val expectedMessage = "pong"
        `when`(rabbitTemplate.receiveAndConvert(queueName)).thenReturn(expectedMessage)

        val actualMessage = microserviceApplication.receiveMessage(queueName)

        assertEquals(expectedMessage, actualMessage)
        verify(rabbitTemplate, times(1)).receiveAndConvert(queueName)
    }
}
