#!/bin/sh

# Exit immediately if a command exits with a non-zero status.
set -e

# Define the Ollama service host. In Render and Docker Compose, this is the service name.
OLLAMA_HOST="ollama:11434"

# --- Wait for Ollama Service ---
echo "Waiting for Ollama service to be available at $OLLAMA_HOST..."
# Use a loop to poll the Ollama health endpoint.
# The '-sS' flags make curl silent but show errors.
# The '--fail' flag makes curl return a non-zero exit code on HTTP errors.
until curl -sS --fail "http://$OLLAMA_HOST" > /dev/null 2>&1; do
  echo "Ollama not yet available, waiting 5 seconds..."
  sleep 5
done

echo "Ollama service is up."

# --- Pull the Model ---
echo "Pulling the llama3 model from Ollama..."
ollama pull llama3 --host "$OLLAMA_HOST"

echo "Model pull complete. Starting Spring Boot application..."
# --- Start the Application ---
exec java -jar app.jar
