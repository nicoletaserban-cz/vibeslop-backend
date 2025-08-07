# Stage 1: Build the application using a full JDK
FROM eclipse-temurin:17-jdk-jammy as builder

# Set the working directory in the container
WORKDIR /app

# Copy the Maven wrapper and project definition files
# This is done in a separate layer to leverage Docker's layer caching.
# These files don't change often, so this layer will be cached.
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies. This will also be cached if pom.xml doesn't change.
RUN ./mvnw dependency:go-offline

# Copy the rest of the application's source code
COPY src ./src

# Package the application into a JAR file, skipping tests
RUN ./mvnw package -DskipTests

# Stage 2: Create the final, smaller image with only the JRE
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Install curl (needed for the run.sh script) and the Ollama CLI.
# This allows this container to communicate with the Ollama service to pull models.
RUN apt-get update && apt-get install -y curl && \
    curl -L https://ollama.com/download/ollama-linux-amd64 -o /usr/bin/ollama && \
    chmod +x /usr/bin/ollama && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar
# Copy the startup script
COPY run.sh .

# Expose the port the app runs on
EXPOSE 8080

# The command to run the startup script
ENTRYPOINT ["./run.sh"]