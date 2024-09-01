#!/bin/sh

# Host and port to ping
HOST="rabbitmq"
PORT="5672"

# Wait until RabbitMQ is reachable
echo "Waiting for RabbitMQ to be ready at $HOST:$PORT..."

# Loop until RabbitMQ responds
while ! nc -z $HOST $PORT; do
  echo "RabbitMQ is not ready yet. Retrying in 2 seconds..."
  sleep 2
done

echo "RabbitMQ is up and running at $HOST:$PORT. Starting Spring Boot application..."

# Run the Spring Boot application with Gradle
./gradlew bootRun