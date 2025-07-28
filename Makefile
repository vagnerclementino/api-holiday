# Data-Oriented Programming Holiday API - Makefile
# Spring Boot 3 + MongoDB 8 + Docker Compose

include MakefileDocumentation

# Configuration
SERVICE_NAME := holiday-api
APP_NAME := holiday-api
DOCKER_COMPOSE := docker-compose
MAVEN := ./mvnw
JAVA_OPTS := -Xmx512m -Xms256m

# Default target
.DEFAULT_GOAL := help

# Check if Docker is running
docker-check:
	@docker info >/dev/null 2>&1 || (echo "$(RED)❌ Docker is not running. Please start Docker first.$(NC)" && exit 1)

# Build and run targets
build-artifact: ##@application Build the Spring Boot application and generate JAR
	@echo "$(GREEN)Building Spring Boot application and generating JAR...$(NC)"
	@$(MAVEN) clean package -DskipTests
	@echo "$(GREEN)JAR artifact created: target/$(APP_NAME)-*.jar$(NC)"
	@echo "$(GREEN)Build completed successfully!$(NC)"

build-image: build-artifact docker-check ##@application Build Docker image from local JAR
	@echo "$(GREEN)Building Docker image from local JAR...$(NC)"
	@if [ ! -f target/$(APP_NAME)-*.jar ]; then \
		echo "$(RED)❌ JAR file not found. Run 'make build-artifact' first.$(NC)"; \
		exit 1; \
	fi
	@docker build --no-cache -t $(SERVICE_NAME):latest -f Dockerfile .
	@echo "$(GREEN)Docker image built successfully!$(NC)"

run: build-artifact ##@application Build and run the complete application (like the reference project)
	@echo "$(GREEN)Building and starting Holiday API...$(NC)"
	@echo "$(YELLOW)Starting MongoDB first...$(NC)"
	@$(DOCKER_COMPOSE) up mongodb -d
	@echo "$(YELLOW)Waiting for MongoDB to be ready...$(NC)"
	@sleep 10
	@echo "$(GREEN)Starting Spring Boot application...$(NC)"
	@$(MAVEN) spring-boot:run -Dspring-boot.run.profiles=local

run-docker: build-image ##@application Build and run with Docker containers
	@echo "$(GREEN)Building and starting Holiday API with Docker...$(NC)"
	@$(DOCKER_COMPOSE) up --build

run-only: docker-check ##@application Run application without building (containers only)
	@echo "$(GREEN)Starting application containers...$(NC)"
	@$(DOCKER_COMPOSE) up

run-detached: build-image ##@application Build and run in background
	@echo "$(GREEN)Starting Holiday API in background...$(NC)"
	@$(DOCKER_COMPOSE) up --build -d
	@echo "$(GREEN)Application started! Check status with 'make status'$(NC)"

# Infrastructure targets
infra: docker-check ##@infra Start only the infrastructure (MongoDB)
	@echo "$(GREEN)Starting infrastructure services...$(NC)"
	@$(DOCKER_COMPOSE) up mongodb

db: docker-check ##@infra Start only MongoDB database
	@echo "$(GREEN)Starting MongoDB database...$(NC)"
	@$(DOCKER_COMPOSE) up mongodb

# Development targets
dev: ##@development Start development mode (same as run)
	@$(MAKE) run

dev-debug: ##@development Start development mode with debug enabled
	@echo "$(GREEN)Starting development mode with debug...$(NC)"
	@echo "$(YELLOW)Debug port: 5005$(NC)"
	@echo "$(YELLOW)Starting MongoDB first...$(NC)"
	@$(DOCKER_COMPOSE) up mongodb -d
	@sleep 10
	@$(MAVEN) spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

# Database targets
mongosh: docker-check ##@database Connect to MongoDB shell
	@echo "$(GREEN)Connecting to MongoDB...$(NC)"
	@docker exec -it mongodb mongosh -u holiday_user -p holiday_pass_2024 mongodb://localhost:27017/holiday-api

mongo-admin: docker-check ##@database Connect to MongoDB as admin
	@echo "$(GREEN)Connecting to MongoDB as admin...$(NC)"
	@docker exec -it mongodb mongosh -u holiday_admin -p holiday_admin_2024 mongodb://localhost:27017/admin

db-reset: docker-check ##@database Reset database with fresh data
	@echo "$(YELLOW)Resetting database...$(NC)"
	@docker exec -it mongodb mongosh -u holiday_admin -p holiday_admin_2024 --eval "db.getSiblingDB('holiday-api').dropDatabase()" mongodb://localhost:27017/admin
	@$(DOCKER_COMPOSE) restart mongodb
	@echo "$(GREEN)Database reset completed!$(NC)"

# Testing targets
test: ##@tests Run API integration tests
	@echo "$(GREEN)Running API integration tests...$(NC)"
	@if ! $(MAKE) status-local >/dev/null 2>&1; then \
		echo "$(YELLOW)API not running. Please start it first with 'make run'$(NC)"; \
		exit 1; \
	fi
	@sleep 2
	@./scripts/test-api.sh
	@echo "$(GREEN)Integration tests completed!$(NC)"

unit-test: ##@tests Run unit tests
	@echo "$(GREEN)Running unit tests...$(NC)"
	@$(MAVEN) test
	@echo "$(GREEN)Unit tests completed!$(NC)"

test-all: ##@tests Run all tests (unit + integration)
	@echo "$(GREEN)Running all tests...$(NC)"
	@$(MAKE) unit-test
	@$(MAKE) test
	@echo "$(GREEN)All tests completed!$(NC)"

# Control targets
stop: ##@control Stop all containers and processes
	@echo "$(YELLOW)Stopping all services...$(NC)"
	@$(DOCKER_COMPOSE) down 2>/dev/null || echo "$(YELLOW)No containers to stop$(NC)"
	@pkill -f "spring-boot:run" 2>/dev/null || echo "$(YELLOW)No Spring Boot processes to stop$(NC)"
	@echo "$(GREEN)All services stopped$(NC)"

restart: ##@control Restart all services
	@echo "$(YELLOW)Restarting all services...$(NC)"
	@$(MAKE) stop
	@$(MAKE) run
	@echo "$(GREEN)All services restarted$(NC)"

clean: ##@control Clean up everything (containers, volumes, build artifacts)
	@echo "$(YELLOW)Cleaning up everything...$(NC)"
	@$(DOCKER_COMPOSE) down -v --remove-orphans 2>/dev/null || true
	@docker system prune -f 2>/dev/null || true
	@$(MAVEN) clean
	@rm -f app.log app.pid
	@echo "$(GREEN)Cleanup completed$(NC)"

# Status and monitoring targets
status: ##@monitoring Check service status
	@echo "$(GREEN)Service Status:$(NC)"
	@echo -n "MongoDB: "
	@if $(DOCKER_COMPOSE) ps mongodb 2>/dev/null | grep -q "Up"; then \
		echo "$(GREEN)Running$(NC)"; \
	else \
		echo "$(RED)Stopped$(NC)"; \
	fi
	@$(MAKE) status-local

status-local: ##@monitoring Check local API status
	@echo -n "API Health: "
	@if curl -s http://localhost:8080/actuator/health >/dev/null 2>&1; then \
		echo "$(GREEN)Healthy$(NC)"; \
	else \
		echo "$(RED)Unhealthy$(NC)"; \
	fi

logs: docker-check ##@monitoring View MongoDB logs
	@echo "$(GREEN)Viewing MongoDB logs:$(NC)"
	@$(DOCKER_COMPOSE) logs -f mongodb

# Utility targets
url: ##@utility Show API URLs
	@echo "$(GREEN)API URLs:$(NC)"
	@echo "  Main API: http://localhost:8080/api/holidays"
	@echo "  Swagger UI: http://localhost:8080/swagger-ui.html"
	@echo "  API Docs: http://localhost:8080/api-docs"
	@echo "  Health Check: http://localhost:8080/actuator/health"
	@echo "  Home: http://localhost:8080/"

health: ##@utility Check API health
	@echo "$(GREEN)Checking API health...$(NC)"
	@curl -s http://localhost:8080/actuator/health | jq . 2>/dev/null || curl -s http://localhost:8080/actuator/health || echo "$(RED)API not responding$(NC)"

sample-data: ##@utility Load sample holiday data
	@echo "$(GREEN)Loading sample data...$(NC)"
	@./scripts/load-sample-data.sh

quick-test: ##@utility Quick API test
	@echo "$(GREEN)Quick API test...$(NC)"
	@curl -s http://localhost:8080/ | jq . 2>/dev/null || curl -s http://localhost:8080/ || echo "$(RED)API not responding$(NC)"

# Package and deployment targets
package: ##@deployment Package application for production (alias for build-artifact)
	@$(MAKE) build-artifact

# Environment info
info: ##@utility Show environment information
	@echo "$(GREEN)Environment Information:$(NC)"
	@echo "Java Version: $$(java -version 2>&1 | head -n 1)"
	@echo "Maven Version: $$($(MAVEN) -version 2>&1 | head -n 1)"
	@echo "Docker Version: $$(docker --version 2>/dev/null || echo 'Not available')"
	@echo "Docker Compose Version: $$($(DOCKER_COMPOSE) --version 2>/dev/null || echo 'Not available')"
	@echo "MongoDB Version: $$($(DOCKER_COMPOSE) exec mongodb mongosh --eval 'db.version()' --quiet 2>/dev/null || echo 'Not running')"

# Setup target for complete initialization
setup: ##@setup Complete environment setup
	@echo "$(GREEN)🚀 Setting up Holiday API environment...$(NC)"
	@$(MAKE) clean
	@$(MAKE) build-artifact
	@echo "$(GREEN)✅ Build completed!$(NC)"
	@echo "$(YELLOW)📋 Next steps:$(NC)"
	@echo "  1. Run the application: make run"
	@echo "  2. Check URLs: make url"
	@echo "  3. Load sample data: make sample-data"
	@echo "$(GREEN)🎉 Holiday API is ready for development!$(NC)"

.PHONY: docker-check build-artifact build-image run run-docker run-only run-detached infra db dev dev-debug mongosh mongo-admin db-reset test unit-test test-all stop restart clean status status-local logs url health sample-data quick-test package info setup help
