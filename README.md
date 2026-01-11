# Report Service

A core analytics microservice in the Traversium platform responsible for aggregating metrics, generating usage reports, and calculating costs for tenants.
The service communicates with the Audit Service via gRPC to gather raw activity data and persists historical snapshots for trend analysis.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Running the Service](#running-the-service)
- [API Documentation](#api-documentation)
- [Architecture](#architecture)
- [Integration](#integration)
- [Monitoring and Health](#monitoring-and-health)

## Overview

The Report Service provides Traversium Tenants and administrators with:

- Metrics on user activity, trip creation, and social interactions.
- Monitoring of storage consumption (GB) and API call volume.
- Automated calculation of monthly costs.
- Historical analysis of growth and usage trends over time.

## Features

### Tenant Reporting
- Retrieve consolidated reports including active users, storage, and costs.
- View historical metric points (time-series) for dashboard visualizations.

### Specialized Metrics
- **User Metrics**: Total vs. active users and new registrations.
- **Trip Metrics**: Statistics on trip creation.
- **Media Metrics**: Storage tracking and media upload volumes.
- **Social Metrics**: Aggregated counts of likes, comments, and general interactions.

### Resilience & Performance
- gRPC Communication to retrieve data from the Audit Service.
- Resilience4j Circuit Breakers and Retries for external service calls.
- Caching of calculated metrics in PostgreSQL for fast retrieval of historical reports.

## Prerequisites

- Java 17
- Kotlin 1.9.25
- Maven 3.6+
- PostgreSQL 12+
- Travesium Audit Service
- Kafka cluster (for Spring Cloud Bus configuration refresh)
- Config Server (optional, for centralized management)

## Configuration

### Application Properties

The service is configured via `application.properties`. Key configurations include gRPC client settings and database connectivity:

```properties
# Application
spring.application.name=TraversiumReportService
server.port=8086

# gRPC Client (Connection to Audit Service)
spring.grpc.client.audit.host=localhost
spring.grpc.client.audit.port=9092

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/traversium
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true

# Kafka & Config
spring.kafka.bootstrap-servers=localhost:9092
spring.config.import=optional:configserver:http://localhost:8888

# Actuator & Monitoring
management.endpoints.web.exposure.include=health,info,prometheus,refresh,busrefresh
```

## Running the Service

### Local Development

```bash
# Run with Maven
mvn spring-boot:run

# Or build and run JAR
mvn clean package
java -jar target/TraversiumReportService-1.1.0.jar
```

### Using Docker

```bash
# Build Docker image
docker build -t traversium-report-service .

# Run container
docker run -p 8086:8086 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/traversium \
  -e SPRING_GRPC_CLIENT_AUDIT_HOST=audit-service \
  traversium-report-service
```

## API Documentation

### Swagger UI

Once the service is running, the OpenAPI documentation is available at:
```
http://localhost:8086/swagger-ui.html
```

### Key Endpoints

- `GET /rest/v1/reports/tenant/{tenantId}` - Get comprehensive tenant report.
- `GET /rest/v1/reports/tenant/{tenantId}/users` - Get detailed user-related metrics.
- `GET /rest/v1/reports/tenant/{tenantId}/trips` - Get trip-related metrics.
- `GET /rest/v1/reports/tenant/{tenantId}/media` - Get storage and media metrics.
- `GET /rest/v1/reports/tenant/{tenantId}/pricing` - Get current billing breakdown and pricing model.


## Integration

### Traversium Ecosystem
- **Audit Service**: primary data source via gRPC.
- **Common Multitenancy**: shared library for tenant context propagation.
- **Config Server**: centralized property management.

### External Systems
- **PostgreSQL**: stores historical snapshots of metrics and calculated costs.
- **ELK Stack**: structured logging via Logstash Logback Encoder.
- **Prometheus**: scrapes performance and business metrics.
- **Firebase / Google Cloud**: tenant creation

## Monitoring and Health

### Health Endpoints
- **Liveness**: `/actuator/health/liveness`
- **Readiness**: `/actuator/health/readiness`
- **Metrics**: `/actuator/prometheus`

Key metrics:
- JVM metrics (memory, threads, GC)
- HTTP request metrics
- Database connection pool metrics
- Custom business metrics

### Logging

Logs are structured in JSON format (Logstash encoder) for ELK Stack integration:
- Application logs: Log4j2
- Request/response logging
- Error tracking