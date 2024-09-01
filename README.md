# Ping Pong Application

## Overview

The Ping Pong Application is a microservices-based project built using Spring Boot and Kotlin, designed to demonstrate interservice communication through RabbitMQ. The application consists of two microservices, each running in a Docker container, with communication managed through RabbitMQ message queues.

## High-Level Requirements

- **Microservices**: The application comprises two microservices built with Spring Boot and Kotlin.
- **Communication**: The microservices communicate using RabbitMQ.
- **Containerization**: All services are fully dockerized for easy deployment.

## High-Level Design

- **Microservices**:
  - **M1**: Spring Boot-based service.
  - **M2**: Kotlin (v1.9) based service.
- **RabbitMQ**:
  - RabbitMQv3 is used for interservice communication.
  - Two queues are utilized for each microservice.
  - Direct Exchange setup.
  - The RabbitMQ broker is containerized.
- **Docker**:
  - Each microservice has its own Dockerfile creating a JDK 8 image.
  - Services are managed and orchestrated using Docker Compose.

## Main Functions

Both microservices (M1 & M2) implement the following core functions:

- **`logMessage(message)`**: Logs the message along with the timestamp.
- **`sendMessage(queue, message)`**: Sends a message to the specified queue.
- **`receiveMessage(queue)`**: Receives/consumes a message from the specified queue.
- **`startMessaging()`**: Runs an infinite loop as an MQ listener, taking appropriate actions based on received messages.

## Setup Instructions

1. **Clone the Repository**:
   ```bash
   git clone git@github.com:rivera-davidkyle/ping-pong-ms.git
   cd ping-pong-ms
   docker compose up
   ```
