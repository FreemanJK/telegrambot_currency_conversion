# Use an official OpenJDK runtime as the base image
FROM openjdk:17

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/springbootTelegramBotOlzhas-0.0.1-SNAPSHOT.jar app.jar

# Create the voice_messages directory
RUN mkdir -p /app/voice_messages


# Copy the JSON key file into the container
COPY speechtotextgoogle.json /app/key.json

# Expose the port that the application is listening on
EXPOSE 9090

# Set the entrypoint command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
