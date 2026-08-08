# 📐 Architectural Decision Records (DECISIONS.md)

### Decision 1: Microservice Decomposition vs Monolith for High-Concurrency Seat Holds
- **Options Considered**:
  1. Monolithic Spring Boot Application.
  2. Microservices Architecture with dedicated `inventory-service` (Seat Hold Engine) and `payment-service`.
- **What We Chose**: Option 2 — Microservices Architecture.
- **Why**: High-concurrency spikes on premiere seats (*Spider-Man: Brand New Day*) must not degrade movie browsing or authentication services. Decoupling the atomic seat locking engine (`inventory-service`) isolates database lock contention.
- **Trade-off / What We Gave Up**: Higher initial setup complexity, inter-service network latency, and service discovery overhead.

---

### Decision 2: Atomic DB State Updates vs In-Memory Redis Locking
- **Options Considered**:
  1. Distributed Redis Lock (Redlock).
  2. Atomic Database Update with `SELECT ... FOR UPDATE` & `UPDATE ... WHERE status = 'AVAILABLE'`.
- **What We Chose**: Option 2 — Atomic SQL Updates with Pessimistic DB Row Locking.
- **Why**: Guarantees zero oversell (Oversell = 0) with strict ACID compliance directly on the relational database without requiring external cluster state synchronization during container startup.
- **Trade-off / What We Gave Up**: Slightly higher DB IOPS under extreme burst loads compared to memory-only locks, compensated by DB connection pooling.

---

### Decision 3: Idempotent Asynchronous Webhooks for Gateway Misbehavior
- **Options Considered**:
  1. Synchronous waiting on `/charge` response until payment completion.
  2. Asynchronous non-blocking `202 Accepted` response with idempotent webhook listener (`processed_events` table).
- **What We Chose**: Option 2 — Asynchronous non-blocking pattern with webhook event deduplication.
- **Why**: The provided mock gateway exhibits delays (2-15s), guaranteed 10% payment failures, and 8% duplicate callbacks. Synchronous waiting would block HTTP connection pools and cause client timeouts.
- **Trade-off / What We Gave Up**: Eventual consistency model requiring status polling on the frontend.
