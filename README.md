# Data-Oriented Programming Holiday API

[![Quality Assurance](https://github.com/vagnerclementino/api-holiday/actions/workflows/quality.yml/badge.svg)](https://github.com/vagnerclementino/api-holiday/actions/workflows/quality.yml)
[![Quality Assurance - Main](https://github.com/vagnerclementino/api-holiday/actions/workflows/quality.yml/badge.svg?branch=main)](https://github.com/vagnerclementino/api-holiday/actions/workflows/quality.yml)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/projects/jdk/25/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-8-green.svg)](https://www.mongodb.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A comprehensive example demonstrating **Data-Oriented Programming (DOP) v1.1** principles in Java, built as a REST API for managing public holidays across different countries, states, and cities using **Spring Boot 3.5.6** and **MongoDB 8**.

This project follows the architectural patterns from [Bootify.io](https://bootify.io/) and implements a data model inspired by the [Holiday API](https://holidayapi.com/docs).

## 🚀 Quick Start

### Prerequisites

- **Java 25** (required)
- **Docker & Docker Compose** (for MongoDB)

### Setup and Run

1. **Clone and start:**

   ```bash
   git clone https://github.com/vagnerclementino/api-holiday.git
   cd api-holiday
   ./mvnw spring-boot:run
   ```

2. **Access the API:**
   - API: <http://localhost:8080/api/holidays>
   - Swagger: <http://localhost:8080/swagger-ui.html>

The application automatically starts MongoDB via Docker Compose using `spring-boot-docker-compose`.

### Development Commands

| Command | Description |
|---------|-------------|
| `./mvnw spring-boot:run` | Run the application |
| `./mvnw test` | Run unit tests |
| `./mvnw clean package` | Build JAR |
| `docker-compose up mongodb` | Start only MongoDB |

> **Need Java 25?** See [CONTRIBUTING.md](CONTRIBUTING.md) for installation guide.

## 📮 Postman Collections

This project includes comprehensive **Postman Collections** for testing all API endpoints and DOP patterns. The collections are organized to demonstrate the complete functionality of the Holiday API with real-world test scenarios.

### 🚀 **Quick Start with Postman**

1. **Import the Collections:**
   - Navigate to the [`postman/`](./postman/) directory
   - Import `DOP-Holiday-API.postman_collection.json` into Postman
   - Import `DOP-Holiday-API.postman_environment.json` for environment variables

2. **Start Testing:**
   - Select the **🎯 DOP Holiday API - Master Environment**
   - Ensure the API is running at `http://localhost:8080`
   - Run individual requests or the complete test suite

### 📊 **Collection Structure**

The Postman collection includes **46 comprehensive tests** organized into **5 main categories**:

| Category | Tests | Description |
|----------|-------|-------------|
| **🟢 Basic CRUD Operations** | ~30 | Create, Read, Update, Delete holidays |
| **🔵 Advanced Filtering** | ~5 | Complex queries and filtering |
| **🟡 Validation & Errors** | ~5 | Input validation and error handling |
| **🟠 DOP-Specific Types** | ~4 | Data-oriented holiday types |
| **🔴 Performance Tests** | ~2 | Response time and load testing |

### 🏗️ **DOP Types Covered**

The collection includes specific tests for all **Data-Oriented Programming** holiday types:

- **📅 Fixed Holidays** - Christmas, New Year (fixed dates)
- **👁️ Observed Holidays** - Holidays with mondayisation rules
- **🔄 Moveable Holidays** - Easter, Thanksgiving (calculated dates)
- **🔗 Moveable From Base** - Good Friday (calculated from other holidays)

### 📚 **Complete Documentation**

For detailed instructions, environment variables, and advanced usage, see:
**[📖 Postman Collections Documentation](./postman/README.md)**

---

## 📚 API Usage Examples

### Create a Holiday

```bash
curl -X POST "http://localhost:8080/api/holidays" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "Fixed",
    "name": "New Year",
    "description": "New Year celebration",
    "day": 1,
    "month": "JANUARY",
    "year": 2024,
    "localities": [
      {
        "localityType": "Country",
        "code": "BR",
        "name": "Brazil"
      }
    ],
    "holidayType": "NATIONAL"
  }'
```

### List All Holidays

```bash
curl "http://localhost:8080/api/holidays"
```

### Filter Holidays

```bash
# By country
curl "http://localhost:8080/api/holidays?country=Brazil"

# By type
curl "http://localhost:8080/api/holidays?type=NATIONAL"

# By date range
curl "http://localhost:8080/api/holidays?startDate=2024-01-01&endDate=2024-12-31"
```

### Update a Holiday

```bash
curl -X PUT "http://localhost:8080/api/holidays/{holiday-id}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Holiday Name",
    "date": "2024-01-01",
    "country": "Brazil",
    "type": "NATIONAL",
    "description": "Updated description"
  }'
```

### Delete a Holiday

```bash
curl -X DELETE "http://localhost:8080/api/holidays/{holiday-id}"
```

## 🧪 Testing

### 📮 Postman Collections (Recommended)

```bash
# Import collections from postman/ directory
# See detailed guide: ./postman/README.md
```

### Run Unit Tests

```bash
make unit-test
# or
./mvnw test
```

### Run Integration Tests

```bash
make test
```

The integration tests demonstrate:

- Complete data flow from API to database
- Validation at boundaries
- Immutable data transformations
- Error handling and edge cases

**For comprehensive API testing with real scenarios, use the Postman collections in [`postman/`](./postman/) directory.**

## 🎯 Quality Assurance

This project includes a comprehensive **GitHub Actions workflow** for automated quality assurance that runs on every pull request.

### 🏷️ **Build Status**

The badges at the top of this README show the current status of our quality checks:

- **Quality Assurance**: Status of the latest workflow run
- **Quality Assurance - Main**: Status specifically for the main branch
- **Java 25**: Version compatibility
- **Spring Boot 3.5.6**: Framework version
- **MongoDB 8**: Database version

### 🚀 **Quality Workflow**

The `quality.yml` workflow ensures code quality through:

| Stage | Description | Tools |
|-------|-------------|-------|
| **🏗️ Build** | Compile source and test code, create JAR | Maven, Java 25 |
| **🎨 Style Check** | Code style and formatting validation | Checkstyle, Spotless |
| **🧪 Unit Tests** | Execute unit tests with coverage | JUnit, Surefire |
| **🔗 Integration Tests** | Run integration tests with MongoDB | TestContainers, MongoDB 8 |
| **🚪 Quality Gate** | Aggregate results and block merge if failed | GitHub Actions |

### 📋 **Local Quality Commands**

```bash
# Run complete quality workflow locally
make quality

# Individual quality checks
make checkstyle          # Run Checkstyle analysis
make format-check        # Check code formatting
make format-fix          # Auto-fix formatting issues
make style-check         # Run all style checks
make pre-commit          # Pre-commit quality checks

# Test commands
make unit-test           # Run unit tests only
make test               # Run integration tests
make test-all           # Run all tests
```

### 🛡️ **Branch Protection**

Pull requests to `main` branch require:

- ✅ All quality checks must pass
- ✅ Code review approval
- ✅ Branch must be up to date

**No code can be merged without passing the complete quality workflow.**

## 📁 Project Structure

```
src/
├── main/java/me/clementino/holiday/
│   ├── domain/          # Domain objects (immutable records)
│   │   ├── dop/         # Data-Oriented Programming approach
│   │   │   ├── Holiday.java
│   │   │   ├── Location.java
│   │   │   ├── HolidayType.java
│   │   │   ├── FixedHoliday.java
│   │   │   ├── MoveableHoliday.java
│   │   │   └── HolidayOperations.java
│   │   └── oop/         # Object-Oriented Programming approach
│   │       ├── Holiday.java
│   │       ├── Locality.java
│   │       └── FixedHoliday.java
│   ├── dto/             # Data Transfer Objects
│   │   ├── CreateHolidayRequestDTO.java
│   │   ├── HolidayResponseDTO.java
│   │   ├── LocationInfoDTO.java
│   │   └── HolidayQueryDTO.java
│   ├── validation/      # Boundary validation
│   │   └── ValidationResult.java
│   ├── mapper/          # Pure transformation functions
│   │   ├── HolidayMapper.java
│   │   └── HolidayCreationMapper.java
│   ├── service/         # Business logic
│   │   └── HolidayService.java
│   ├── repository/      # Data persistence
│   │   └── HolidayRepository.java
│   ├── controller/      # REST Controllers
│   │   └── HolidayController.java
│   ├── entity/          # MongoDB entities
│   │   └── HolidayEntity.java
│   └── exception/       # Exception handling
│       └── GlobalExceptionHandler.java
├── test/java/me/clementino/holiday/
│   └── HolidayApiIntegrationTest.java
└── postman/             # Postman Collections & Documentation
    ├── DOP-Holiday-API.postman_collection.json
    ├── DOP-Holiday-API.postman_environment.json
    └── README.md        # Complete Postman documentation
```

## 🏛️ Infrastructure

The project uses:

- **Spring Boot 3.5.6**: Modern Java web framework
- **MongoDB 8.0.12**: Document-oriented NoSQL database
- **Docker Compose**: Container orchestration
- **Maven**: Build automation and dependency management
- **Make**: Task automation and convenience commands

## 🎓 Learning Outcomes

By exploring this project, you'll understand:

1. **How to design immutable data structures** that prevent bugs
2. **How to validate at system boundaries** for clean architecture
3. **How to use pure functions** for predictable transformations
4. **How to make illegal states unrepresentable** with type safety
5. **How to build REST APIs** with Spring Boot 3.5.6 and modern Java
6. **How to test data-oriented systems** effectively
7. **How to use Docker Compose** for local development
8. **How to create comprehensive API test suites** with Postman Collections

## 🤝 Contributing

Contribuições são bem-vindas! Este projeto serve como exemplo educacional de Data-Oriented Programming.

**📋 Leia o [Guia de Contribuição](CONTRIBUTING.md)** para informações detalhadas sobre:

- Como reportar bugs e sugerir funcionalidades
- Processo de desenvolvimento e code review
- Padrões de código e convenções
- Workflow de qualidade obrigatório
- Proteção de branch e regras de merge

Contribuições podem incluir:

- 🐛 Correção de bugs
- ✨ Novas funcionalidades DOP
- 📚 Melhorias na documentação
- 🧪 Testes adicionais
- 🎨 Novos exemplos de programação orientada a dados

## 📖 Related Articles

This project accompanies a series of 3 articles about **"Nem tudo é objeto: Programação Orientada a Dados em Java"** which provide detailed explanations of the concepts demonstrated here:

- [Parte 1: Fundamentos da Programação Orientada a Dados](https://notes.clementino.me/blog/nem-tudo-eh-objeto-parte-1/)
- [Parte 2: Implementação Prática em Java](https://notes.clementino.me/blog/nem-tudo-eh-objeto-parte-2/)
- [Parte 3: Comparação com Programação Orientada a Objetos](https://notes.clementino.me/blog/nem-tudo-eh-objeto-parte-3/)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
