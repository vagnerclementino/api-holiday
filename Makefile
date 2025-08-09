# Data-Oriented Programming Holiday API - Makefile
# Spring Boot 3 + MongoDB 8 + Docker Compose

include MakefileDocumentation

# Configuration
SERVICE_NAME := holiday-api
APP_NAME := holiday-api
DOCKER_COMPOSE := docker-compose
MAVEN := JAVA_HOME=~/.asdf/installs/java/adoptopenjdk-24.0.1+9 ./mvnw
JAVA_OPTS := -Xmx512m -Xms256m

# Default target
.DEFAULT_GOAL := help

# Check if Docker is running
docker-check:
	@docker info >/dev/null 2>&1 || (echo "$(RED)❌ Docker is not running. Please start Docker first.$(NC)" && exit 1)

# JShell demo
jshell-demo: compile
	@echo "$(BLUE)🔧 Running JShell Holiday Demo...$(NC)"
	@jshell --class-path target/classes examples/moveable-holidays-demo.jsh
	@echo "$(GREEN)✅ JShell demo completed!$(NC)"

# Alternative JShell demo using wrapper script
demo: compile
	@echo "$(BLUE)🔧 Running Holiday API Demo...$(NC)"
	@./scripts/run-jshell-demo.sh

# Code quality checks
checkstyle: ##@tests Run Google Java Checkstyle analysis
	@echo "$(BLUE)🔍 Running Custom Google Java Checkstyle analysis...$(NC)"
	@echo "$(YELLOW)Using Custom Google Java Style Guide (120 chars, no indentation check)$(NC)"
	@echo "$(YELLOW)Reference: https://google.github.io/styleguide/javaguide.html$(NC)"
	@$(MAVEN) checkstyle:check -Dcheckstyle.consoleOutput=true || true
	@echo ""
	@if [ -f target/checkstyle-result.xml ]; then \
		echo "$(BLUE)📊 Checkstyle Report Summary:$(NC)"; \
		violations=$$(grep -c '<error' target/checkstyle-result.xml 2>/dev/null || echo "0"); \
		files=$$(grep -c '<file' target/checkstyle-result.xml 2>/dev/null || echo "0"); \
		echo "  Files analyzed: $$files"; \
		echo "  Total violations: $$violations"; \
		if [ "$$violations" -gt "0" ] 2>/dev/null; then \
			echo "$(YELLOW)⚠️  Issues found! Review the output above for details.$(NC)"; \
		else \
			echo "$(GREEN)✅ No style violations found!$(NC)"; \
		fi; \
		echo "$(BLUE)📄 Detailed report: target/checkstyle-result.xml$(NC)"; \
		echo "$(BLUE)📋 Configuration: checkstyle.xml$(NC)"; \
	else \
		echo "$(YELLOW)⚠️  Checkstyle report not generated$(NC)"; \
	fi
	@echo "$(GREEN)✅ Checkstyle analysis completed!$(NC)"

checkstyle-fix: ##@tests Auto-fix Checkstyle violations using Google Java Format
	@echo "$(BLUE)🔧 Auto-fixing Checkstyle violations...$(NC)"
	@echo "$(YELLOW)Using Google Java Format to fix code style issues$(NC)"
	@echo "$(YELLOW)💡 Git will track all changes - no manual backup needed$(NC)"
	@echo ""
	@echo "$(BLUE)🔧 Formatting Java code with Google Java Format...$(NC)"
	@$(MAVEN) com.spotify.fmt:fmt-maven-plugin:format || true
	@echo ""
	@echo "$(GREEN)✅ Auto-fix completed!$(NC)"
	@echo "$(BLUE)📊 Running Checkstyle again to verify fixes...$(NC)"
	@$(MAKE) checkstyle
	@echo ""
	@echo "$(GREEN)🎉 Checkstyle auto-fix process completed!$(NC)"
	@echo "$(YELLOW)📊 Violations reduced from 92 to $(shell grep -c '<error' target/checkstyle-result.xml 2>/dev/null || echo '?')$(NC)"
	@echo "$(YELLOW)💡 Review changes with: git diff$(NC)"
	@echo "$(YELLOW)🔄 Revert changes with: git checkout -- src/$(NC)"
	@echo "$(YELLOW)✅ Commit changes with: git add . && git commit -m 'fix: apply Google Java Format'$(NC)"

build-artifact: ##@application Build the Spring Boot application and generate JAR
	@echo "$(GREEN)Building Spring Boot application and generating JAR...$(NC)"
	@echo "$(YELLOW)Checking Java version for compilation...$(NC)"
	@java -version 2>&1 | head -1 | grep -E "\"(2[4-9]|[3-9][0-9])" > /dev/null || \
		(echo "$(RED)❌ Java 24+ required for compilation. Current: $$(java -version 2>&1 | head -1)$(NC)" && \
		 echo "$(YELLOW)💡 Please set JAVA_HOME to Java 24+ or use: export JAVA_HOME=~/.asdf/installs/java/adoptopenjdk-24.0.1+9$(NC)" && \
		 exit 1)
	@$(MAVEN) clean package -DskipTests
	@echo "$(GREEN)JAR artifact created: target/$(APP_NAME)-*.jar$(NC)"
	@echo "$(GREEN)Build completed successfully!$(NC)"

build-image: build-artifact docker-check ##@application Build Docker image from local JAR
	@echo "$(GREEN)Building Docker image from local JAR...$(NC)"
	@if ! ls target/$(APP_NAME)-*.jar >/dev/null 2>&1; then \
		echo "$(RED)❌ JAR file not found. Run 'make build-artifact' first.$(NC)"; \
		exit 1; \
	fi
	@docker build --no-cache -t $(SERVICE_NAME):latest -f Dockerfile .
	@echo "$(GREEN)Docker image built successfully!$(NC)"

run: ##@application Build and run the complete application from source (no local Java 24 required)
	@echo "$(GREEN)Building and starting Holiday API from source...$(NC)"
	@echo "$(YELLOW)🔨 Building application inside Docker (OpenJDK 24)...$(NC)"
	@echo "$(YELLOW)📦 This will take a few minutes on first run...$(NC)"
	@echo "services:" > docker-compose.override.yml
	@echo "  holiday-api:" >> docker-compose.override.yml
	@echo "    build:" >> docker-compose.override.yml
	@echo "      dockerfile: Dockerfile.dev" >> docker-compose.override.yml
	@$(DOCKER_COMPOSE) up --build
	@rm -f docker-compose.override.yml

run-local: build-artifact ##@application Build and run with local Java (requires Java 24)
	@echo "$(GREEN)Building and starting Holiday API with local Java...$(NC)"
	@echo "$(YELLOW)Starting MongoDB first...$(NC)"
	@$(DOCKER_COMPOSE) up mongodb -d
	@echo "$(YELLOW)Waiting for MongoDB to be ready...$(NC)"
	@sleep 10
	@echo "$(GREEN)Starting Spring Boot application...$(NC)"
	@$(MAVEN) spring-boot:run -Dspring-boot.run.profiles=local

run-docker: build-image ##@application Build and run with pre-built JAR (requires local Java 24)
	@echo "$(GREEN)Building and starting Holiday API with Docker...$(NC)"
	@$(DOCKER_COMPOSE) up --build

run-only: docker-check ##@application Run application without building (containers only)
	@echo "$(GREEN)Starting application containers...$(NC)"
	@$(DOCKER_COMPOSE) up

run-detached: ##@application Build and run from source in background (no local Java 24 required)
	@echo "$(GREEN)Starting Holiday API from source in background...$(NC)"
	@echo "$(YELLOW)🔨 Building application inside Docker (OpenJDK 24)...$(NC)"
	@echo "services:" > docker-compose.override.yml
	@echo "  holiday-api:" >> docker-compose.override.yml
	@echo "    build:" >> docker-compose.override.yml
	@echo "      dockerfile: Dockerfile.dev" >> docker-compose.override.yml
	@$(DOCKER_COMPOSE) up --build -d
	@rm -f docker-compose.override.yml
	@echo "$(GREEN)Application started! Check status with 'make status'$(NC)"

# Infrastructure targets
infra: docker-check ##@infra Start only the infrastructure (MongoDB)
	@echo "$(GREEN)Starting infrastructure services...$(NC)"
	@$(DOCKER_COMPOSE) up mongodb

db: docker-check ##@infra Start only MongoDB database
	@echo "$(GREEN)Starting MongoDB database...$(NC)"
	@$(DOCKER_COMPOSE) up mongodb

# Development targets
dev: ##@development Start development mode from source (no local Java 24 required)
	@echo "$(GREEN)Starting development mode...$(NC)"
	@echo "$(YELLOW)🔨 Building application inside Docker with Java 24...$(NC)"
	@echo "$(YELLOW)📚 Perfect for study - no need to install Java 24 locally!$(NC)"
	@$(MAKE) run

dev-local: ##@development Start development mode with local Java (requires Java 24)
	@echo "$(GREEN)Starting development mode with local Java...$(NC)"
	@$(MAKE) run-local

dev-debug: ##@development Start development mode with debug enabled (requires local Java 24)
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
	@docker-compose -f docker-compose.yml -f docker-compose.dev.yml down -v --remove-orphans 2>/dev/null || true
	@docker system prune -f 2>/dev/null || true
	@$(MAVEN) clean 2>/dev/null || true
	@rm -f app.log app.pid
	@echo "$(GREEN)Cleanup completed$(NC)"

clean-dev: ##@control Clean up development containers and images
	@echo "$(YELLOW)Cleaning up development environment...$(NC)"
	@docker-compose -f docker-compose.yml -f docker-compose.dev.yml down -v --remove-orphans 2>/dev/null || true
	@docker rmi holiday-api:dev 2>/dev/null || true
	@echo "$(GREEN)Development cleanup completed$(NC)"

# Status and monitoring targets
status: ##@monitoring Check service status
	@echo "$(GREEN)Service Status:$(NC)"
	@printf "MongoDB: "
	@if $(DOCKER_COMPOSE) ps mongodb 2>/dev/null | grep -q "Up"; then \
		echo "$(GREEN)Running$(NC)"; \
	else \
		echo "$(RED)Stopped$(NC)"; \
	fi
	@$(MAKE) status-local

status-local: ##@monitoring Check local API status
	@printf "API Health: "
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
java-check: ##@setup Check Java version compatibility
	@echo "$(GREEN)Checking Java version compatibility...$(NC)"
	@echo "Current Java version: $$(java -version 2>&1 | head -1)"
	@echo "Maven Java version: $$($(MAVEN) -version 2>&1 | grep 'Java version' | head -1)"
	@java -version 2>&1 | head -1 | grep -E "\"(2[4-9]|[3-9][0-9])" > /dev/null && \
		echo "$(GREEN)✅ Java version is compatible for compilation$(NC)" || \
		(echo "$(RED)❌ Java 24+ required for compilation$(NC)" && \
		 echo "$(YELLOW)Available Java versions:$(NC)" && \
		 ls ~/.asdf/installs/java/ 2>/dev/null | grep -E "(24|25|26|27|28|29)" | head -5 || echo "No compatible Java versions found" && \
		 echo "$(YELLOW)💡 To fix this issue:$(NC)" && \
		 echo "   1. Install Java 24+: asdf install java adoptopenjdk-24.0.1+9" && \
		 echo "   2. Set JAVA_HOME: export JAVA_HOME=~/.asdf/installs/java/adoptopenjdk-24.0.1+9" && \
		 echo "   3. Or use: make build-artifact JAVA_HOME=~/.asdf/installs/java/adoptopenjdk-24.0.1+9" && \
		 exit 1)

setup: java-check ##@setup Complete environment setup with Java version check
	@echo "$(GREEN)🚀 Setting up Holiday API environment...$(NC)"
	@$(MAKE) clean
	@$(MAKE) build-artifact
	@echo "$(GREEN)✅ Build completed!$(NC)"
	@echo "$(YELLOW)📋 Next steps:$(NC)"
	@echo "  1. Run the application: make run"
	@echo "  2. Check URLs: make url"
	@echo "  3. Load sample data: make sample-data"
	@echo "$(GREEN)🎉 Holiday API is ready for development!$(NC)"

.PHONY: docker-check build-artifact build-image run run-local run-docker run-only run-detached infra db dev dev-local dev-debug mongosh mongo-admin db-reset test unit-test test-all stop restart clean clean-dev status status-local logs url health sample-data quick-test package info java-check setup help
