# java-generic-project

A minimal Spring Boot 3 REST API project using Maven and JDK 17 that connects to Redis and PostgreSQL databases, with OpenTelemetry tracing via Jaeger.

---

## Features

- Headless REST API (no UI) with 2 endpoints:
    - GET from Redis key-value store
    - GET from PostgreSQL database
- Data model includes two entities:
    - Customer
    - Address (linked to Customer)
- OpenTelemetry support with Jaeger tracing integration
- Dockerized environment with Redis, PostgreSQL, and Jaeger all in containers
- Automated start and stop scripts that handle service dependencies and readiness checks
- TraceID created per API request and propagated to backend calls, traceable in Jaeger UI

---

## Prerequisites

- Java 17 JDK installed
- Maven installed OR use Maven Wrapper (`./mvnw`)
- Docker and Docker Compose installed and running
- Optional: Browser for accessing Jaeger UI

---

## Setup & Run

### 1. Build and start services

Use the provided start script which:

- Checks Docker availability and ports
- Starts Jaeger first, then Redis, PostgreSQL, then the Spring Boot app
- Opens Jaeger UI in your browser automatically (if supported)

```bash
./start-generic-project.sh