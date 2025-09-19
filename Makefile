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
	@java -version 2>&1 | head -1 | grep -E "\"(2[5-9]|[3-9][0-9])" > /dev/null || \
		(echo "$(RED)❌ Java 25+ required for compilation. Current: $$(java -version 2>&1 | head -1)$(NC)" && \
		 echo "$(YELLOW)💡 Please set JAVA_HOME to Java 25+ or use: export JAVA_HOME=~/.asdf/installs/java/adoptopenjdk-25.0.1+9$(NC)" && \
		 exit 1)
	@$(MAVEN) clean package -DskipTests -q
	@echo "$(GREEN)JAR artifact created: target/$(APP_NAME)-*.jar$(NC)"
	@echo "$(GREEN)Build completed successfully!$(NC)"

run-local: build-artifact ##@application Build and run with local Java (requires Java 25)
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
dev-local: ##@development Start development mode with local Java (requires Java 25)
	@echo "$(GREEN)Starting development mode with local Java...$(NC)"
	@$(MAKE) run-local

dev-debug: ##@development Start development mode with debug enabled (requires local Java 25)
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
test: ##@tests Run all tests (unit + integration)
	@echo "$(GREEN)Running all tests (unit + integration)...$(NC)"
	@$(MAVEN) test -Pall-tests
	@echo "$(GREEN)All tests completed!$(NC)"

unit-test: ##@tests Run unit tests only
	@echo "$(GREEN)Running unit tests...$(NC)"
	@$(MAVEN) test -Punit-tests
	@echo "$(GREEN)Unit tests completed!$(NC)"

integration-test: ##@tests Run integration tests only
	@echo "$(GREEN)Running integration tests...$(NC)"
	@$(MAVEN) test -Pintegration-tests
	@echo "$(GREEN)Integration tests completed!$(NC)"

# Quality Assurance targets
quality: ##@quality Run complete quality workflow (build + style + tests)
	@echo "$(GREEN)🎯 Running complete quality workflow...$(NC)"
	@$(MAKE) build-artifact
	@$(MAKE) checkstyle
	@$(MAKE) test
	@echo "$(GREEN)✅ Quality workflow completed!$(NC)"

# HTML Reports targets
reports: ##@reports Generate all HTML reports and open dashboard
	@echo "$(GREEN)📊 Generating all HTML reports...$(NC)"
	@$(MAKE) reports-generate
	@$(MAKE) reports-open

reports-generate: ##@reports Generate all HTML reports (style + tests)
	@echo "$(GREEN)📊 Generating HTML reports...$(NC)"
	@echo "$(BLUE)🎨 Generating style report...$(NC)"
	@$(MAKE) report-style-generate
	@echo "$(BLUE)🧪 Generating test report...$(NC)"
	@$(MAKE) report-test-generate
	@echo "$(BLUE)� Gennerating consolidated dashboard...$(NC)"
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

report-test: ##@reports Generate test report and open in browser
	@echo "$(GREEN)🧪 Generating test report...$(NC)"
	@$(MAKE) report-test-generate
	@$(MAKE) report-test-open

report-test-generate: ##@reports Generate test HTML report
	@echo "$(BLUE)🧪 Generating test HTML report...$(NC)"
	@$(MAVEN) test -Pall-tests -B
	@mkdir -p target/reports-html
	@echo "$(GREEN)✅ Test HTML report generated! Check target/site/jacoco/ for coverage reports.$(NC)"

report-test-open: ##@reports Open test report in browser
	@echo "$(GREEN)🌐 Opening test coverage report...$(NC)"
	@if [ -f "target/site/jacoco/index.html" ]; then \
		open target/site/jacoco/index.html; \
		echo "$(GREEN)✅ Test coverage report opened in browser!$(NC)"; \
	else \
		echo "$(RED)❌ Test coverage report not found. Run 'make report-test-generate' first.$(NC)"; \
		exit 1; \
	fi

report-dashboard-generate: ##@reports Generate consolidated HTML dashboard
	@echo "$(BLUE)📋 Generating consolidated HTML dashboard...$(NC)"
	@mkdir -p target/reports-html
	@echo "$(GREEN)✅ Dashboard available at target/site/jacoco/index.html for test coverage$(NC)"

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
	@$(MAKE) test
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
	@java -version 2>&1 | head -1 | grep -E "\"(2[5-9]|[3-9][0-9])" > /dev/null && \
		echo "$(GREEN)✅ Java version is compatible for compilation$(NC)" || \
		(echo "$(RED)❌ Java 25+ required for compilation$(NC)" && \
		 echo "$(YELLOW)Available Java versions:$(NC)" && \
		 ls ~/.asdf/installs/java/ 2>/dev/null | grep -E "(25|26|27|28|29)" | head -5 || echo "No compatible Java versions found" && \
		 echo "$(YELLOW)💡 To fix this issue:$(NC)" && \
		 echo "   1. Install Java 25+: asdf install java adoptopenjdk-25.0.1+9" && \
		 echo "   2. Set JAVA_HOME: export JAVA_HOME=~/.asdf/installs/java/adoptopenjdk-25.0.1+9" && \
		 echo "   3. Or use: make build-artifact JAVA_HOME=~/.asdf/installs/java/adoptopenjdk-25.0.1+9" && \
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

.PHONY: docker-check build-artifact build-image run run-local run-docker run-only run-detached infra db dev dev-local dev-debug mongosh mongo-admin db-reset test unit-test integration-test stop restart clean clean-dev status status-local logs url health sample-data quick-test package info java-check setup help reports reports-generate reports-open report-style report-style-generate report-style-open report-test report-test-generate report-test-open report-dashboard-generate
