# Technology Stack & Build System

## Core Technologies

- **Java 24** with preview features enabled (records, sealed interfaces, pattern matching)
- **Spring Boot 3.5.4** with Spring Framework 6.2.9
- **MongoDB 8** for document storage
- **Maven 3.9.11** for build automation
- **Docker & Docker Compose** for containerization

## Key Dependencies

- `spring-boot-starter-web` - REST API framework
- `spring-boot-starter-data-mongodb` - MongoDB integration
- `spring-boot-starter-validation` - Bean validation
- `springdoc-openapi-starter-webmvc-ui` - API documentation (Swagger)
- `testcontainers` - Integration testing with containers

## Build Commands

### Development (No Local Java Required)

```bash
make run          # Build and run from source in Docker
make dev          # Start development mode
make run-detached # Run in background
```

### Local Development (Requires Java 24)

```bash
make run-local    # Build and run with local Java
make dev-local    # Development mode with local Java
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### Testing

```bash
make test         # Run API integration tests
make unit-test    # Run unit tests (./mvnw test)
make test-all     # Run all tests
```

### Build & Package

```bash
make build-artifact  # Build JAR (./mvnw clean package -DskipTests)
make package        # Alias for build-artifact
```

### Code Quality

```bash
make checkstyle     # Run Google Java Style analysis
make checkstyle-fix # Auto-fix style violations
```

### Infrastructure

```bash
make infra         # Start MongoDB only
make db            # Start MongoDB only
make mongosh       # Connect to MongoDB shell
```

## Java 24 Configuration

- Preview features automatically enabled via Maven
- JVM arguments: `--enable-preview`
- Compilation target: Java 24
- Runtime optimized for modern Java features
