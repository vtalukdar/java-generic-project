#!/bin/bash

set -e

echo "Stopping Spring Boot application if running..."
SPRING_PID=$(lsof -ti tcp:8080 || true)
if [ -n "$SPRING_PID" ]; then
  echo "Killing Spring Boot process (PID: $SPRING_PID)..."
  kill "$SPRING_PID" || true
  wait "$SPRING_PID" 2>/dev/null || true
  echo "Spring Boot stopped."
else
  echo "Spring Boot not running."
fi

echo "Stopping Docker services (Jaeger, Redis, Postgres)..."
docker-compose down

echo "All services stopped successfully."
