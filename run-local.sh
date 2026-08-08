#!/bin/bash
# CinemaSeat - Local Development Runner
# Starts all Spring Boot microservices and the Vue 3 frontend

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$PROJECT_DIR/backend"
FRONTEND_DIR="$PROJECT_DIR/frontend"
PID_FILE="$PROJECT_DIR/.backend_pids"

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

start_services() {
  echo -e "${BLUE}🎬 CinemaSeat - Starting All Services${NC}"
  echo ""
  > "$PID_FILE"

  # 1. Start Docker dependencies (PostgreSQL + Mock Gateway)
  echo -e "${GREEN}🐳 Starting Docker dependencies (PostgreSQL + Mock Gateway)...${NC}"
  docker compose -f "$PROJECT_DIR/docker-compose.yml" up -d postgres gateway 2>/dev/null || true
  sleep 3

  # 2. Build shared library
  echo -e "${GREEN}📦 Building shared-library...${NC}"
  cd "$BACKEND_DIR"
  mvn clean install -pl shared-library -am -DskipTests -q 2>/dev/null || {
    echo -e "${RED}⚠️ Shared library build failed, trying full build...${NC}"
    mvn clean install -DskipTests -q
  }

  # 3. Start Config Server
  echo -e "${GREEN}🔧 Starting Config Server (port 8888)...${NC}"
  cd "$BACKEND_DIR"
  mvn spring-boot:run -pl platform/config-server -DskipTests > "$PROJECT_DIR/config-server.log" 2>&1 &
  echo $! >> "$PID_FILE"
  sleep 8

  # 4. Start Discovery Server (Eureka)
  echo -e "${GREEN}🔍 Starting Discovery Server (port 8761)...${NC}"
  mvn spring-boot:run -pl platform/discovery-server -DskipTests > "$PROJECT_DIR/discovery-server.log" 2>&1 &
  echo $! >> "$PID_FILE"
  sleep 8

  # 5. Start API Gateway
  echo -e "${GREEN}🌐 Starting API Gateway (port 8080)...${NC}"
  mvn spring-boot:run -pl platform/api-gateway -DskipTests > "$PROJECT_DIR/api-gateway.log" 2>&1 &
  echo $! >> "$PID_FILE"
  sleep 5

  # 6. Start Business Services
  echo -e "${GREEN}🔐 Starting Auth Service (port 8081)...${NC}"
  mvn spring-boot:run -pl services/auth-service -DskipTests > "$PROJECT_DIR/auth-service.log" 2>&1 &
  echo $! >> "$PID_FILE"

  echo -e "${GREEN}🎬 Starting Product Service (port 8082)...${NC}"
  mvn spring-boot:run -pl services/product-service -DskipTests > "$PROJECT_DIR/product-service.log" 2>&1 &
  echo $! >> "$PID_FILE"

  echo -e "${GREEN}💺 Starting Inventory Service (port 8083)...${NC}"
  mvn spring-boot:run -pl services/inventory-service -DskipTests > "$PROJECT_DIR/inventory-service.log" 2>&1 &
  echo $! >> "$PID_FILE"

  echo -e "${GREEN}💳 Starting Payment Service (port 8084)...${NC}"
  CALLBACK_URL="http://host.docker.internal:8084/api/v1/payments/webhook" mvn spring-boot:run -pl services/payment-service -DskipTests > "$PROJECT_DIR/payment-service.log" 2>&1 &
  echo $! >> "$PID_FILE"

  echo -e "${GREEN}👤 Starting User Service (port 8085)...${NC}"
  mvn spring-boot:run -pl services/user-service -DskipTests > "$PROJECT_DIR/user-service.log" 2>&1 &
  echo $! >> "$PID_FILE"

  sleep 5

  # 7. Start Frontend
  echo -e "${GREEN}🖥️  Starting Vue 3 Frontend (port 5173)...${NC}"
  cd "$FRONTEND_DIR"
  npm run dev > "$PROJECT_DIR/frontend.log" 2>&1 &
  echo $! >> "$PID_FILE"

  echo ""
  echo -e "${BLUE}════════════════════════════════════════════════${NC}"
  echo -e "${GREEN}✅ CinemaSeat is starting up!${NC}"
  echo ""
  echo -e "  Frontend:         ${BLUE}http://localhost:5173${NC}"
  echo -e "  API Gateway:      ${BLUE}http://localhost:8080${NC}"
  echo -e "  Health Check:     ${BLUE}http://localhost:8080/health${NC}"
  echo -e "  Eureka Dashboard: ${BLUE}http://localhost:8761${NC}"
  echo -e "  Mock Gateway:     ${BLUE}http://localhost:9000/health${NC}"
  echo ""
  echo -e "  Demo Login:       zayan@cinemaseat.com / password123"
  echo -e "${BLUE}════════════════════════════════════════════════${NC}"
}

stop_services() {
  echo -e "${RED}🛑 Stopping all CinemaSeat services...${NC}"
  if [ -f "$PID_FILE" ]; then
    while read pid; do
      kill "$pid" 2>/dev/null || true
    done < "$PID_FILE"
    rm -f "$PID_FILE"
  fi
  # Kill any remaining Java/Node processes
  pkill -f "spring-boot:run" 2>/dev/null || true
  pkill -f "vite" 2>/dev/null || true
  echo -e "${GREEN}✅ All services stopped.${NC}"
}

case "${1:-start}" in
  start) start_services ;;
  stop) stop_services ;;
  restart) stop_services; sleep 2; start_services ;;
  *) echo "Usage: $0 {start|stop|restart}" ;;
esac
