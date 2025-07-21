#!/bin/bash

set -e

MAX_RETRIES=5
SLEEP_INTERVAL=5

SPRINGBOOT_PORT=8080
REDIS_PORT=6379
POSTGRES_PORT=5432
JAEGER_PORT=16686

function check_docker() {
  if ! docker info >/dev/null 2>&1; then
    echo "Docker daemon is not running. Please start Docker."
    exit 1
  fi
}

function wait_for_port() {
  local name=$1
  local port=$2
  local retries=0

  echo "Waiting for $name to be available on port $port..."

  until nc -z localhost "$port"; do
    ((retries++))
    if [ "$retries" -ge "$MAX_RETRIES" ]; then
      echo "$name failed to start on port $port after $MAX_RETRIES attempts."
      return 1
    fi
    sleep "$SLEEP_INTERVAL"
  done

  echo "$name is available on port $port."
  return 0
}

function stop_all() {
  echo "Stopping all services..."
  docker-compose down
  if [[ -n "$springboot_pid" ]]; then
    kill "$springboot_pid" || true
    wait "$springboot_pid" 2>/dev/null || true
  fi
  echo "All services stopped."
}

function show_postgres_logs() {
  POSTGRES_CONTAINER=$(docker-compose ps -q postgres)
  if [ -n "$POSTGRES_CONTAINER" ]; then
    echo "==== Postgres logs ===="
    docker logs "$POSTGRES_CONTAINER" | tee postgres_start_error.log
    echo "==== End of Postgres logs ===="
  fi
}

# Start script begins here
check_docker

echo "Starting Jaeger..."
docker-compose up -d jaeger
wait_for_port "Jaeger UI" $JAEGER_PORT || { stop_all; exit 1; }

echo "Starting Redis..."
docker-compose up -d redis
wait_for_port "Redis" $REDIS_PORT || { stop_all; exit 1; }

echo "Starting Postgres..."
docker-compose up -d postgres
if ! wait_for_port "Postgres" $POSTGRES_PORT; then
  echo "Postgres failed to start."
  show_postgres_logs
  stop_all
  exit 1
fi

echo "Starting Spring Boot..."
if [ -f "./mvnw" ]; then
  ./mvnw spring-boot:run &
else
  mvn spring-boot:run &
fi

springboot_pid=$!

if ! wait_for_port "Spring Boot App" $SPRINGBOOT_PORT; then
  echo "Spring Boot failed to start."
  stop_all
  exit 1
fi

# Open Jaeger UI
if command -v xdg-open >/dev/null 2>&1; then
  xdg-open http://localhost:$JAEGER_PORT
elif command -v open >/dev/null 2>&1; then
  open http://localhost:$JAEGER_PORT
else
  echo "Jaeger UI available at http://localhost:$JAEGER_PORT"
fi

echo "All services started successfully!"
echo "Spring Boot App: http://localhost:$SPRINGBOOT_PORT"
echo "Jaeger UI:       http://localhost:$JAEGER_PORT"

# Wait for Spring Boot to exit
wait "$springboot_pid"