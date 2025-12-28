# Stock Trading System

A microservices-based stock trading system built with Spring Boot, featuring real-time market data and order processing with workflow orchestration.

## Overview

This project consists of two main microservices:

- **Market Data Service**: Provides real-time stock prices and market status
- **Order Service**: Handles order creation, validation, execution, and settlement using Temporal workflows

## Architecture

- **Market Data Service** (Port 8081): RESTful API for market data
- **Order Service** (Port 8080): Order management with JWT authentication and PostgreSQL
- **Temporal**: Workflow orchestration for order processing
- **PostgreSQL**: Database for order service
- **Temporal UI**: Web interface for monitoring workflows (Port 8088)

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- PgAdmin

## Getting Started

### 1. Start Infrastructure Services

Start Temporal, PostgreSQL, and Temporal UI using Docker Compose:

```bash
docker-compose up -d
```

This will start:
- PostgreSQL on port `5432`
- Temporal server on port `7233`
- Temporal UI on port `8088` (access at http://localhost:8088)

### 2. Create Database

Create the database for the order service:

```bash
# Connect to PostgreSQL
Open pgAdmin and connect to the local postgres instance 

# Create database
Run order-service/src/main/resources/sql/release-1.sql
\q
```

### 3. Run Market Data Service

```bash
cd market-data-service
./mvnw spring-boot:run
```

The service will start on `http://localhost:8081`

### 4. Run Order Service

```bash
cd order-service
./mvnw spring-boot:run
```

The service will start on `http://localhost:8080`

## API Endpoints

### Market Data Service

- `GET /api/v1/market/status` - Get market status (open/closed)
- `GET /api/v1/market/price/{symbol}` - Get current price for a stock symbol

### Order Service

- `POST /api/v1/auth/register` - Register a new user
- `POST /api/v1/auth/login` - Login and get JWT token
- `POST /api/v1/orders` - Create a new order (requires authentication)
- `GET /api/v1/orders/{orderId}` - Get order details (requires authentication)
- `GET /api/v1/orders` - Get user's orders (requires authentication)
- `GET /api/v1/orders/history` - Get order history (requires authentication)

## Usage Example

1. **Register a user:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"password123","email":"user1@example.com"}'
```

2. **Login:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"password123"}'
```

3. **Create an order (use JWT token from login):**
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"symbol":"AAPL","quantity":10,"orderType":"LIMIT","side":"BUY","limitPrice":199}'
```

4. **Get market price:**
```bash
curl http://localhost:8081/api/v1/market/price/AAPL
```

## Technology Stack

- **Java 17**
- **Spring Boot 4.0.1**
- **Spring Security** - JWT authentication
- **Spring Data JPA** - Database access
- **PostgreSQL** - Relational database
- **Temporal** - Workflow orchestration
- **Docker Compose** - Container orchestration
- **Maven** - Build tool

## Project Structure

```
stock-trading-system/
├── market-data-service/     # Market data microservice
│   └── src/main/java/com/nabeel/market_data_service/
│       ├── controller/       # REST controllers
│       ├── service/          # Business logic
│       └── scheduler/        # Price update scheduler
├── order-service/            # Order processing microservice
│   └── src/main/java/com/nabeel/order_service/
│       ├── controller/       # REST controllers
│       ├── service/          # Business logic
│       ├── temporal/         # Temporal workflows and activities
│       ├── entity/           # JPA entities
│       └── security/         # JWT authentication
└── docker-compose.yml        # Infrastructure setup
```

## Monitoring

- **Temporal UI**: http://localhost:8088 - Monitor workflow executions
- **Swagger/OpenAPI**: http://localhost:8081/swagger-ui/index.html  (Market -data-service)

## License

This project is for educational purposes.

## TODO

1. Proper implementation of Temporal Workflow using saga compensation transactions
2. Move to Gradle build tool
3. Proper testing of Order-service
4. Refactoring and proper exception handling

