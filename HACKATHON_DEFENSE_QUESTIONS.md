# 🎤 Zero to Production: Hackathon Defense & Architecture Questions Guide
### CUET IEEE Computer Society · Student Branch Chapter | Powered by Poridhi.io
**Team Name**: Team 42  
**Project**: E-Commerce Microservices Platform (Java 25 + Vue 3)

---

## 🏛️ Section 1: System Architecture & Design Decisions (25% Weight)

### Q1.1: Why did you choose a Microservices Architecture over a Monolith?
> **Answer**:
> - **Independent Scalability**: High-traffic read operations on the `product-service` can be scaled horizontally independently from write-heavy `order-service` operations.
> - **Domain Isolation**: Isolating domain boundaries prevents a failure in `notification-service` from crashing the order checkout flow.
> - **Autonomous Deployment**: Each microservice maintains its own container lifecycle, enabling zero-downtime updates via Coolify.

### Q1.2: Why Vue 3 (Composition API) instead of React or Angular?
> **Answer**:
> - **Progressive Adoption**: Vue 3 can enhance a single `<div>` or scale into a full Single-Page Application (SPA).
> - **Direct Proxy Reactivity**: Vue 3's native `Proxy` reactivity model eliminates React's stale closure pitfalls, manual memoization (`useMemo`, `useCallback`), and Angular's heavy RxJS boilerplate.
> - **First-Party Ecosystem**: Core libraries (`Vue Router`, `Pinia`) are officially maintained by the core Vue team, guaranteeing version compatibility.

### Q1.3: How are service boundaries defined across the system?
> **Answer**:
> Service boundaries follow Domain-Driven Design (DDD) Bounded Contexts:
> 1. **`auth-service`**: Handles JWT authentication tokens and credential verification (`auth_schema`).
> 2. **`user-service`**: Manages user account profiles and role mappings (`ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_USER`) (`user_schema`).
> 3. **`product-service`**: Handles product listings, category filters, and flash-sale items (`product_schema`).
> 4. **`order-service`**: Manages cart checkout, order persistence (`order_schema`), and publishes RabbitMQ events.
> 5. **`notification-service`**: Asynchronously listens for order events via RabbitMQ AMQP to trigger confirmation emails.

---

## 💾 Section 2: Data Modeling & Schema Isolation

### Q2.1: Why use a single PostgreSQL database (`team42`) with multiple schemas instead of separate database instances?
> **Answer**:
> - **Resource Efficiency**: Running 6 separate database containers during a hackathon consumes excessive memory (RAM). Using a single PostgreSQL instance (`team42`) with dedicated schemas (`user_schema`, `product_schema`, `order_schema`, `auth_schema`) achieves strict data isolation while keeping resource overhead minimal.
> - **Production Parity**: In a cloud environment, each schema can be extracted into an independent database instance without modifying application code.

### Q2.2: How does the User Authorization & Role System work?
> **Answer**:
> - Users are assigned explicit roles (`ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_USER`) initialized via `data.sql` and Java data seeders.
> - `ROLE_ADMIN`: Full administrative control over products and system users.
> - `ROLE_MANAGER`: Inventory management and order fulfillment capabilities.
> - `ROLE_USER`: Standard customer shopping, cart management, and order placement.

---

## 🐰 Section 3: Inter-Service Communication & Event Bus (RabbitMQ)

### Q3.1: Walk us through what happens during an Order Placement request, end-to-end.
> **Answer**:
> 1. **Client Action**: User submits the order on the Vue 3 storefront.
> 2. **Gateway Ingress**: The HTTP POST request hits **Spring Cloud API Gateway** on port `8080` (`/api/v1/orders`).
> 3. **Gateway Routing**: Gateway routes request to **`order-service`** on port `8084`.
> 4. **Database Persistence**: `order-service` writes the order record into `order_schema.orders` in PostgreSQL.
> 5. **Event Emission**: `OrderController` publishes an `OrderPlacedEvent` message to RabbitMQ exchange `team42.orders.exchange`.
> 6. **Async Consumption**: **`notification-service`** receives the message from `team42.orders.queue` via `@RabbitListener` and logs/dispatches the confirmation notification.

### Q3.2: Why use RabbitMQ asynchronous messaging instead of synchronous REST calls?
> **Answer**:
> - **Decoupling & Non-Blocking**: If email dispatching is slow or an external SMTP server drops, synchronous HTTP calls would delay the customer's checkout response. RabbitMQ ensures checkout completes in milliseconds while email dispatching happens asynchronously.
> - **Fault Tolerance**: If `notification-service` is temporarily down, RabbitMQ queues the message until the service recovers, preventing message loss.

---

## ⚡ Section 4: Performance, Bottlenecks & Trade-Offs

### Q4.1: What is the first component that will break under high load?
> **Answer**:
> - **Database Connection Pool**: Under heavy concurrent order writes, PostgreSQL connection limits on `team42` will saturate first.
> - **Mitigation Plan**: Introduce Redis for caching product catalog queries, implement Read Replicas for `product-service`, and configure Resilience4j Circuit Breakers at the Gateway.

### Q4.2: What did you deliberately leave out, and why?
> **Answer**:
> - We used simulated Google OAuth2 login handlers on the frontend while keeping the backend JWT infrastructure fully prepared. This allowed us to focus on shipping complete end-to-end functionality, containerization, and CI/CD pipelines within the 11-hour hackathon window.

---

## 🐳 Section 5: Containerization, CI/CD & Deployment

### Q5.1: How is the application containerized and deployed?
> **Answer**:
> - **Multi-Stage Frontend Dockerfile (`Dockerfile.frontend`)**:
>   - Stage 1: Builds Vue 3 static assets using Node.js v24 Alpine.
>   - Stage 2: Serves static assets using lightweight Nginx Alpine with custom Gzip compression and SPA `try_files` fallback.
> - **Root `docker-compose.yml`**: Orchestra for launching PostgreSQL, RabbitMQ, Gateway, Eureka, Microservices, and Frontend with a single command.
> - **CI/CD (`.github/workflows/build-deploy.yml`)**: GitHub Actions runs Maven & NPM builds on every push to `main` and triggers a deployment webhook to Coolify on Hostinger VPS.

---

## 🏆 Presentation Defense Quick Checklist
- [x] **Repository Public**: Yes (`team42-hackathon`)
- [x] **Root Compose Working**: `docker-compose up --build -d`
- [x] **No Secrets Committed**: Managed via `.env` / `.env.example`
- [x] **Unit Tests Included**: JUnit 5 + Mockito tests in `backend/`
- [x] **Live Demo Ready**: Local dev server on `http://localhost:5173` / Docker on `http://localhost`
