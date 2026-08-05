# Kyronic Risk Engine

Kyronic Risk Engine is a Java 17, Spring Boot 3.x, microservices-based enterprise risk-management backend with strict maker-checker controls for regulated financial institutions.

This repository is organized as a Maven multi-module build with:

- shared cross-cutting libraries
- independently deployable Spring Boot services
- Docker and Kubernetes deployment assets
- observability assets for Prometheus, Grafana, and OpenTelemetry
- TDD-first business implementations, starting with OLTS

## Build

```bash
mvn clean verify
```

## Run core platform locally

```bash
docker compose -f infrastructure/docker-compose.yml up -d
```

## Services

- `api-gateway`
- `auth-service`
- `olts-service`
- `notifications-service`
- `audit-service`
- `kri-service`
- `risk-register-service`
- `process-flows-service`
- `self-assessment-service`
- `document-service`
- `dashboard-service`

## Current delivery scope

This delivery establishes the repository foundation, shared maker-checker/security primitives, gateway and identity foundations, and a TDD-first OLTS implementation with versioned authorization workflow.
