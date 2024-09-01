# Use OpenJDK 8 as the base image
FROM openjdk:8-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper files
COPY gradlew ./
COPY gradle/ gradle/

# Copy the rest of the application files
COPY . .

# Grant execute permission for the Gradle wrapper
RUN chmod +x gradlew
RUN chmod +x scripts/entry_point.sh

# Build the application
RUN ./gradlew build --no-daemon

# Set the entry point to the script
ENTRYPOINT ["sh", "./scripts/entry_point.sh"]