# 🛍️ Ecommerce Microservices

A production-flavored microservices backend built with **Java 21**, **Spring Boot 3.2**, and **Spring Cloud**. Designed as a portfolio project demonstrating service decomposition, inter-service communication via a service registry, and an API Gateway routing layer — all containerised with Docker Compose.

---

## Architecture

                     ┌──────────────────────────┐
                     │      API Gateway          │
                     │   Spring Cloud Gateway    │
                     │       :8080               │
                     └────────────┬─────────────┘
                                  │ routes by path
           ┌──────────────────────┼──────────────────────┐
           │                      │                      │
    /api/users/**          /api/products/**        /api/orders/**
           │                      │                      │
 ┌─────────▼──────┐    ┌──────────▼─────┐    ┌──────────▼─────┐
 │  User Service  │    │ Product Service │    │  Order Service  │
 │     :8081      │    │     :8082       │    │     :8083       │
 └────────┬───────┘    └────────┬────────┘    └───────┬────────┘
          │                     │                     │
     Postgres              Postgres              Postgres  + RabbitMQ
     user_db               product_db            order_db

                ↕ all services register with ↕

              ┌─────────────────────────────┐
              │    Discovery Server          │
              │   Netflix Eureka  :8761      │
              └─────────────────────────────┘

---

## Services

| Service | Port | Description |
|---|---|---|
| `api-gateway` | 8080 | Single entry point — routes all traffic to downstream services |
| `discovery-server` | 8761 | Eureka registry — services register here on startup |
| `user-service` | 8081 | User registration, lookup, and lifecycle management |
| `product-service` | 8082 | Product catalogue with stock tracking |
| `order-service` | 8083 | Order creation and status tracking (in progress) |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2 |
| Service Discovery | Spring Cloud Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Persistence | Spring Data JPA + PostgreSQL |
| Messaging | RabbitMQ (Spring AMQP) |
| Local dev DB | H2 (in-memory, dev profile) |
| Containerisation | Docker + Docker Compose |
| Build | Maven |

---

## Quick Start

### Option A — Run locally without Docker (dev profile)

Each service ships with an H2 in-memory database profile so you can run without any external dependencies.

```bash
# 1. Start the discovery server first
cd discovery-server
mvn spring-boot:run

# 2. In a new terminal, start user-service
cd user-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 3. Optionally start product-service
cd product-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

When user-service is up you'll see this in the logs:

╔══════════════════════════════════════════════════╗
║          USER SERVICE — READY                    ║
╠══════════════════════════════════════════════════╣
║  Base URL : http://localhost:8081/api/users      ║
║  H2 (dev) : http://localhost:8081/h2-console     ║
╚══════════════════════════════════════════════════╝

### Option B — Full stack with Docker Compose

```bash
docker-compose up -d
```

This brings up the three Postgres databases and RabbitMQ. Then start each service individually (or add them to the compose file as you flesh them out).

---

## API Reference — User Service

Base URL: `http://localhost:8081` (direct) or `http://localhost:8080` (via gateway)

### Create a user
```http
POST /api/users
Content-Type: application/json

{
  "username": "alice",
  "email": "alice@example.com",
  "password": "password123"
}
```

Response — `201 Created`:
```json
{
  "id": 1,
  "username": "alice",
  "email": "alice@example.com"
}
```

### List all users
```http
GET /api/users
```

### Get a user by ID
```http
GET /api/users/1
```

### Update a user
```http
PUT /api/users/1
Content-Type: application/json

{
  "username": "alice_updated",
  "email": "alice2@example.com",
  "password": "newpassword123"
}
```

### Delete a user
```http
DELETE /api/users/1
```
Response — `204 No Content`

### Validation errors
Invalid requests return `400` with per-field messages:
```json
{
  "timestamp": "2024-04-16T10:00:00Z",
  "status": 400,
  "error": "Validation failed",
  "fields": {
    "email": "Email must be a valid address",
    "password": "Password must be at least 8 characters"
  }
}
```

---

## Project Structure
ecommerce-microservices/
├── api-gateway/              # Spring Cloud Gateway — routes & load balancing
├── discovery-server/         # Eureka — service registry
├── user-service/             # Users: CRUD, validation, DTO layer
│   └── src/main/java/.../
│       ├── config/           # DataSeeder, StartupListener
│       ├── controller/       # REST endpoints
│       ├── dto/              # UserRequest / UserResponse (no password leak)
│       ├── entity/           # JPA entity
│       ├── exception/        # GlobalExceptionHandler
│       ├── repository/       # Spring Data JPA
│       └── service/          # Business logic layer
├── product-service/          # Products: CRUD, stock management
├── order-service/            # Orders: lifecycle + RabbitMQ events (WIP)
└── docker-compose.yml        # Postgres x3 + RabbitMQ

---

## Roadmap

- [ ] BCrypt password hashing in user-service
- [ ] JWT authentication filter in API Gateway
- [ ] Order → inventory reservation flow via RabbitMQ
- [ ] Full order-service implementation (order lifecycle, status tracking)
- [ ] Resilience patterns — circuit breakers with Resilience4j
- [ ] Centralized configuration with Spring Cloud Config
- [ ] Distributed tracing with Micrometer + Zipkin
- [ ] Integration tests with Testcontainers

---

## Running Tests

```bash
mvn test -pl user-service
```

---

## License

MIT