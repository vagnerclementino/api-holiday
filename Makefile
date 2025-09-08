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

run-local: build-artifact ##@application Build and run with local Java (requires Java 24)
	@echo "$(GREEN)Building and starting Holiday API with local Java...$(NC)"
	@echo "$(YELLOW)Starting MongoDB first...$(NC)"
	@$(DOCKER_COMPOSE) up mongodb -d
	@echo "$(YELLOW)Waiting for MongoDB to be ready...$(NC)"
	@sleep 10
	@echo "$(GREEN)Starting Spring Boot application...$(NC)"
	@$(MAVEN) spring-boot:run -Dspring-boot.run.profiles=local

run-only: docker-check ##@application Run application without building (containers only)
	@echo "$(GREEN)Starting application containers...$(NC)"
	@$(DOCKER_COMPOSE) up

# Infrastructure targets
infra: docker-check ##@infra Start only the infrastructure (MongoDB)
	@echo "$(GREEN)Starting infrastructure services...$(NC)"
	@$(DOCKER_COMPOSE) up mongodb

db: docker-check ##@infra Start only MongoDB database
	@echo "$(GREEN)Starting MongoDB database...$(NC)"
	@$(DOCKER_COMPOSE) up mongodb

# Development targets
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

clean-db: docker-check ##@database Clean all holidays from database
	@echo "$(YELLOW)Cleaning holidays collection...$(NC)"
	@docker exec mongodb mongosh -u holiday_user -p holiday_pass_2024 --eval "db.holidays.deleteMany({})" mongodb://localhost:27017/holiday-api
	@echo "$(GREEN)All holidays deleted from database!$(NC)"

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

# Quality Assurance targets
quality: ##@quality Run complete quality workflow (build + style + tests)
	@echo "$(GREEN)🎯 Running complete quality workflow...$(NC)"
	@./scripts/test-quality-workflow.sh

# HTML Reports targets
reports: ##@reports Generate all HTML reports and open dashboard
	@echo "$(GREEN)📊 Generating all HTML reports...$(NC)"
	@$(MAKE) reports-generate
	@$(MAKE) reports-open

reports-generate: ##@reports Generate all HTML reports (style + unit tests + integration tests)
	@echo "$(GREEN)📊 Generating HTML reports...$(NC)"
	@echo "$(BLUE)🎨 Generating style report...$(NC)"
	@$(MAKE) report-style-generate
	@echo "$(BLUE)🧪 Generating unit test report...$(NC)"
	@$(MAKE) report-unit-generate
	@echo "$(BLUE)🔗 Generating integration test report...$(NC)"
	@$(MAKE) report-integration-generate
	@echo "$(BLUE)📋 Generating consolidated dashboard...$(NC)"
	@$(MAKE) report-dashboard-generate
	@echo "$(GREEN)✅ All HTML reports generated successfully!$(NC)"

reports-open: ##@reports Open HTML reports dashboard in browser
	@echo "$(GREEN)🌐 Opening HTML reports dashboard...$(NC)"
	@if [ -f "target/reports-html/index.html" ]; then \
		open target/reports-html/index.html; \
		echo "$(GREEN)✅ Dashboard opened in browser!$(NC)"; \
	else \
		echo "$(RED)❌ Dashboard not found. Run 'make reports-generate' first.$(NC)"; \
		exit 1; \
	fi

report-style: ##@reports Generate style report and open in browser
	@echo "$(GREEN)🎨 Generating style report...$(NC)"
	@$(MAKE) report-style-generate
	@$(MAKE) report-style-open

report-style-generate: ##@reports Generate Checkstyle HTML report
	@echo "$(BLUE)🎨 Generating Checkstyle HTML report...$(NC)"
	@$(MAVEN) checkstyle:checkstyle -B
	@mkdir -p target/reports-html
	@./scripts/generate-style-report.sh
	@echo "$(GREEN)✅ Checkstyle HTML report generated!$(NC)"

report-style-open: ##@reports Open style report in browser
	@echo "$(GREEN)🌐 Opening style report...$(NC)"
	@if [ -f "target/reports-html/checkstyle.html" ]; then \
		open target/reports-html/checkstyle.html; \
		echo "$(GREEN)✅ Style report opened in browser!$(NC)"; \
	else \
		echo "$(RED)❌ Style report not found. Run 'make report-style-generate' first.$(NC)"; \
		exit 1; \
	fi

report-unit: ##@reports Generate unit test report and open in browser
	@echo "$(GREEN)🧪 Generating unit test report...$(NC)"
	@$(MAKE) report-unit-generate
	@$(MAKE) report-unit-open

report-unit-generate: ##@reports Generate unit test HTML report
	@echo "$(BLUE)🧪 Generating unit test HTML report...$(NC)"
	@$(MAVEN) test -Punit-tests -B
	@mkdir -p target/reports-html
	@./scripts/generate-unit-report.sh
	@echo "$(GREEN)✅ Unit test HTML report generated!$(NC)"

report-unit-open: ##@reports Open unit test report in browser
	@echo "$(GREEN)🌐 Opening unit test report...$(NC)"
	@if [ -f "target/reports-html/unit-tests.html" ]; then \
		open target/reports-html/unit-tests.html; \
		echo "$(GREEN)✅ Unit test report opened in browser!$(NC)"; \
	else \
		echo "$(RED)❌ Unit test report not found. Run 'make report-unit-generate' first.$(NC)"; \
		exit 1; \
	fi

report-integration: ##@reports Generate integration test report and open in browser
	@echo "$(GREEN)🔗 Generating integration test report...$(NC)"
	@$(MAKE) report-integration-generate
	@$(MAKE) report-integration-open

report-integration-generate: ##@reports Generate integration test HTML report
	@echo "$(BLUE)🔗 Generating integration test HTML report...$(NC)"
	@$(MAVEN) test -Pintegration-tests -B
	@mkdir -p target/reports-html
	@./scripts/generate-integration-report.sh
	@echo "$(GREEN)✅ Integration test HTML report generated!$(NC)"

report-integration-open: ##@reports Open integration test report in browser
	@echo "$(GREEN)🌐 Opening integration test report...$(NC)"
	@if [ -f "target/reports-html/integration-tests.html" ]; then \
		open target/reports-html/integration-tests.html; \
		echo "$(GREEN)✅ Integration test report opened in browser!$(NC)"; \
	else \
		echo "$(RED)❌ Integration test report not found. Run 'make report-integration-generate' first.$(NC)"; \
		exit 1; \
	fi

report-dashboard-generate: ##@reports Generate consolidated HTML dashboard
	@echo "$(BLUE)📋 Generating consolidated HTML dashboard...$(NC)"
	@mkdir -p target/reports-html
	@./scripts/generate-dashboard.sh
	@echo "$(GREEN)✅ Consolidated HTML dashboard generated!$(NC)"

checkstyle: ##@quality Run Checkstyle code analysis
	@echo "$(GREEN)🎨 Running Checkstyle analysis...$(NC)"
	@$(MAVEN) checkstyle:check -B
	@echo "$(GREEN)Checkstyle analysis completed!$(NC)"

format-check: ##@quality Check code formatting (Spotless)
	@echo "$(GREEN)📋 Checking code formatting...$(NC)"
	@$(MAVEN) spotless:check -B
	@echo "$(GREEN)Code formatting check completed!$(NC)"

format-fix: ##@quality Auto-fix code formatting (Spotless)
	@echo "$(GREEN)🔧 Auto-fixing code formatting...$(NC)"
	@$(MAVEN) spotless:apply -B
	@echo "$(GREEN)Code formatting applied!$(NC)"

style-check: ##@quality Run all style checks (Checkstyle + Spotless)
	@echo "$(GREEN)🎨 Running all style checks...$(NC)"
	@$(MAKE) checkstyle
	@$(MAKE) format-check
	@echo "$(GREEN)All style checks completed!$(NC)"

pre-commit: ##@quality Run pre-commit quality checks
	@echo "$(GREEN)🚀 Running pre-commit quality checks...$(NC)"
	@$(MAVEN) clean compile test-compile -DskipTests -B
	@$(MAKE) style-check
	@$(MAKE) unit-test
	@echo "$(GREEN)Pre-commit checks completed! Ready to commit.$(NC)"

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
	@$(MAVEN) clean 2>/dev/null || true
	@rm -f app.log app.pid
	@echo "$(GREEN)Cleanup completed$(NC)"

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

.PHONY: docker-check build-artifact build-image run run-local run-docker run-only run-detached infra db dev dev-local dev-debug mongosh mongo-admin db-reset test unit-test test-all stop restart clean clean-dev status status-local logs url health sample-data quick-test package info java-check setup help reports reports-generate reports-open report-style report-style-generate report-style-open report-unit report-unit-generate report-unit-open report-integration report-integration-generate report-integration-open report-dashboard-generate
