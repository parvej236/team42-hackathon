# 🛠️ Team 42 Backend Microservices Suite (Java Spring Boot 3 + Spring Cloud)

Welcome to the backend engineering documentation for **Team 42 E-Commerce Platform**.

---

## 🏗️ Architecture & Module Map

```
backend/
├── pom.xml                         # Parent POM (group id: com.team42)
├── shared-library/                 # Shared Java Records & Feign DTOs
├── platform/
│   ├── config-server/              # Spring Cloud Config Server (Port 8888)
│   ├── discovery-server/           # Netflix Eureka Service Registry (Port 8761)
│   └── api-gateway/                # Spring Cloud API Gateway (Port 8080)
└── services/
    ├── auth-service/               # JWT Authentication (Port 8081)
    ├── user-service/               # Profiles & Multi-Role User Initializer (Port 8082)
    ├── product-service/            # Product Catalog & Category Search (Port 8083)
    ├── inventory-service/          # Stock Management (Port 8086)
    ├── order-service/              # Checkout & AMQP Event Producer (Port 8084)
    ├── payment-service/            # Payment Integration (Port 8087)
    └── notification-service/       # RabbitMQ AMQP Email Consumer (Port 8085)
```

---

## 💻 Step-by-Step Execution Guide (Maven & IntelliJ IDEA)

Follow this exact canonical Spring Cloud startup sequence to run the backend:

### Step 1: Infrastructure Prerequisites
1. **Initialize PostgreSQL Database & Schemas**:
   ```sh
   sudo -u postgres psql -f ../docker/postgres/init-schemas.sql
   ```
2. **Start RabbitMQ Event Broker**:
   ```sh
   docker start rabbitmq
   ```

### Step 2: Build Parent Maven Module
```sh
mvn clean install -DskipTests
```

### Step 3: Launch Microservices in Canonical Order

#### 1️⃣ `config-server` (Port 8888) — **START FIRST**
- **Maven CLI**:
  ```sh
  mvn spring-boot:run -pl platform/config-server
  ```
- **IntelliJ IDEA**: Right-click `ConfigServerApplication.java` -> **Run**

#### 2️⃣ `discovery-server` / Eureka Registry (Port 8761) — **START SECOND**
- **Maven CLI**:
  ```sh
  mvn spring-boot:run -pl platform/discovery-server
  ```
- **IntelliJ IDEA**: Right-click `DiscoveryServerApplication.java` -> **Run**
- **Verify**: Open `http://localhost:8761` in browser.

#### 3️⃣ Business Microservices — **START THIRD (Any Order)**
- **`auth-service`** (Port 8081):
  `mvn spring-boot:run -pl services/auth-service`
- **`user-service`** (Port 8082):
  `mvn spring-boot:run -pl services/user-service`
- **`product-service`** (Port 8083):
  `mvn spring-boot:run -pl services/product-service`
- **`inventory-service`** (Port 8086):
  `mvn spring-boot:run -pl services/inventory-service`
- **`order-service`** (Port 8084):
  `mvn spring-boot:run -pl services/order-service`
- **`payment-service`** (Port 8087):
  `mvn spring-boot:run -pl services/payment-service`
- **`notification-service`** (Port 8085):
  `mvn spring-boot:run -pl services/notification-service`

#### 4️⃣ `api-gateway` (Port 8080) — **START LAST**
- **Maven CLI**:
  ```sh
  mvn spring-boot:run -pl platform/api-gateway
  ```
- **IntelliJ IDEA**: Right-click `ApiGatewayApplication.java` -> **Run**

---

## 🔍 Verification Matrix

| Service | Port | Endpoint / Verification |
| :--- | :---: | :--- |
| **Config Server** | `8888` | `http://localhost:8888/actuator/health` |
| **Eureka Registry** | `8761` | `http://localhost:8761` (Dashboard) |
| **API Gateway** | `8080` | `http://localhost:8080/api/v1/products` (Catalog API) |
| **RabbitMQ UI** | `15672` | `http://localhost:15672` (`guest`/`guest`) |
