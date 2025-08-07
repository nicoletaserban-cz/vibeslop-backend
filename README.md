# Vibeslop Backend

This is a Spring Boot application that uses Spring AI and a locally running Ollama instance to generate technical interview questions based on a given set of technologies.

## Prerequisites

Before you begin, ensure you have the following installed on your local machine:

- **Java 17** or higher
- **Apache Maven**
- **Container Runtime**: To run the application with Docker (recommended).
  - **Docker Desktop**: The standard, easy-to-use application for Mac and Windows.
  - **Colima**: A great open-source alternative for macOS and Linux users.

## Getting Started

The easiest way to run the application and its dependencies is with Docker and Docker Compose. This setup will create containers for both the backend application and the Ollama service, creating a fully self-contained environment.

### 1. Run with Docker Compose

1.  **Ensure your container runtime (like Docker Desktop or Colima) is running.**
2.  **Build and start the services:**

    > **Note for Colima users:** If you haven't already, install it with `brew install colima docker docker-compose` and start it with `colima start`. The `docker-compose` command below will work without any changes.

    ```bash
    docker-compose up --build -d
    ```

This command builds the application image and starts both the `vibeslop-backend` and `ollama` services in detached mode (`-d`).

### 2. Pull the Language Model

Once the containers are running, you need to pull the required language model (`llama3`) into the Ollama container. This only needs to be done once, as the model will be stored in a Docker volume.

Open a new terminal and run the following command:

```bash
docker exec -it ollama ollama run llama3
```

This will download the model inside the container. Once the download is complete and you see the prompt, you can type `/bye` to exit. The model will now be available to the application.

The application will be available at `http://localhost:8080`. To stop all services, run `docker-compose down`.

### Alternative: Run with Maven (Without Docker)

If you prefer not to use Docker, you can run the application directly with Maven. **Note:** This method requires you to install and run Ollama on your host machine separately.

1. **Install and Run Ollama on your host:** Follow the instructions on the official Ollama website. Once installed, pull the `llama3` model:
   ```bash
   ollama run llama3
   ```

2. **Run the Spring Boot application:**
   ```bash
   ./mvnw spring-boot:run
   ```

Or, if you have Maven installed globally:

```bash
mvn spring-boot:run
```

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

## Deploy to Render

This project is configured for one-click deployment on Render.

[!Deploy to Render](https://render.com/deploy?repo=https://github.com/nicoletaserban-cz/vibeslop-backend)

Clicking the button above will deploy the application based on the `render.yaml` file in this repository.

### How It Works

The `render.yaml` file defines two services:

1.  **`vibeslop-backend` (Web Service)**: This is the main Spring Boot application. It uses the `Dockerfile` to build a container. A startup script (`run.sh`) ensures that it waits for the Ollama service to be ready and automatically pulls the `llama3` model before launching the application.
2.  **`ollama` (Private Service)**: This service runs the official `ollama/ollama` Docker image. It is not exposed to the public internet but is accessible to the backend service. It has a persistent disk attached, so the downloaded `llama3` model is saved across deploys and restarts.

### Environment Variables

The `render.yaml` file sets most environment variables. However, for security, you should manage sensitive values like `SPRING_JWT_SECRET` directly in the Render dashboard. The configuration is set to auto-generate a secure value for you on the first deploy.

After your first deploy, the application will be live at the URL provided by Render. The initial startup may take a few minutes as it needs to download the 4.7GB `llama3` model to its persistent disk. Subsequent deploys will be much faster.

## API Endpoints

Once the application is running, you can interact with its API.
