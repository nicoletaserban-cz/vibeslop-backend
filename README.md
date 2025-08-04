# Vibeslop Backend

This is a Spring Boot application that uses Spring AI and a locally running Ollama instance to generate technical interview questions based on a given set of technologies.

## Prerequisites

Before you begin, ensure you have the following installed on your local machine:

- **Java 17** or higher
- **Apache Maven**
- **Ollama**: You can download it from the [official Ollama website](https://ollama.com/).

## Getting Started

Follow these steps to get the application up and running.

### 1. Install and Run Ollama

First, install Ollama by following the instructions for your operating system on their website.

Once installed, the application requires a specific large language model to function. The project is configured by default to use `llama3`.

Open your terminal and pull the model by running:

```bash
ollama run llama3
```

This command will download the model. Once the download is complete and you see the prompt, you can type `/bye` to exit. Ollama will continue to run as a background service.

### 2. Run the Spring Boot Application

You can run the application using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

Or, if you have Maven installed globally:

```bash
mvn spring-boot:run
```

The server will start on `http://localhost:8080`.

## API Endpoints

Once the application is running, you can interact with its API.

### Generate Interview Questions

- **Endpoint**: `POST /api/interviews/generate`
- **Description**: Generates a list of technical interview questions for the technologies provided in the request body.
- **Request Body**: A JSON object with a list of technologies.
  ```json
  {
    "technologies": ["Java", "Spring Boot", "PostgreSQL"]
  }
  ```
- **Example `curl` command**:
  ```bash
  curl -X POST \
    http://localhost:8080/api/interviews/generate \
    -H 'Content-Type: application/json' \
    -d '{"technologies": ["Java", "Spring Boot", "PostgreSQL"]}'
  ```

## Configuration

The primary configuration is located in `src/main/resources/application.properties`. Here you can change the Ollama settings, such as the base URL or the model name.

```properties
# Spring AI Ollama Configuration
spring.ai.ollama.base-url=http://localhost:11434

# Specify the model to use (e.g., llama3, mistral, etc.)
spring.ai.ollama.chat.options.model=llama3
```
