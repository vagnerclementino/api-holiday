# Data-Oriented Programming Holiday API

[![Quality Assurance](https://github.com/vagnerclementino/odp-api-holiday/actions/workflows/quality.yml/badge.svg)](https://github.com/vagnerclementino/odp-api-holiday/actions/workflows/quality.yml)
[![Quality Assurance - Main](https://github.com/vagnerclementino/odp-api-holiday/actions/workflows/quality.yml/badge.svg?branch=main)](https://github.com/vagnerclementino/odp-api-holiday/actions/workflows/quality.yml)
[![Java](https://img.shields.io/badge/Java-24-orange.svg)](https://openjdk.org/projects/jdk/24/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-8-green.svg)](https://www.mongodb.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A comprehensive example demonstrating **Data-Oriented Programming (DOP) v1.1** principles in Java, built as a REST API for managing public holidays across different countries, states, and cities using **Spring Boot 3** and **MongoDB 8**.

This project follows the architectural patterns from [Bootify.io](https://bootify.io/) and implements a data model inspired by the [Holiday API](https://holidayapi.com/docs).

## 🎯 Data-Oriented Programming v1.1 Principles Demonstrated

This project showcases the four refined principles of Data-Oriented Programming v1.1:

### 1. **Model Data Immutably and Transparently**
- All domain objects are implemented as Java `records`
- Transparent data carriers with full field access
- Immutable by design - operations return new instances
- Thread-safe and predictable

```java
public record Holiday(
    String id,
    String name,
    LocalDate date,
    Location location,
    HolidayType type,
    boolean recurring,
    Optional<String> description
) {
    // Compact constructor for validation
    public Holiday {
        Objects.requireNonNull(name, "Holiday name cannot be null");
        Objects.requireNonNull(date, "Holiday date cannot be null");
        Objects.requireNonNull(location, "Holiday location cannot be null");
        Objects.requireNonNull(type, "Holiday type cannot be null");
    }
    
    // Transformation methods instead of setters
    public Holiday withName(String newName) {
        return new Holiday(id, newName, date, location, type, recurring, description);
    }
}
```

### 2. **Model the Data, the Whole Data, and Nothing but the Data**
- Domain objects represent complete business entities
- Tailored aggregates using records
- Sealed interfaces for modeling alternatives
- No artificial abstractions or unnecessary complexity

```java
public record Location(
    String country,
    Optional<String> state,
    Optional<String> city
) {
    // Models exactly what a location is - no more, no less
    public Location {
        Objects.requireNonNull(country, "Country cannot be null");
        if (country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be blank");
        }
    }
}

// Sealed interface for modeling validation results
public sealed interface ValidationResult 
    permits ValidationResult.Success, ValidationResult.Failure {
    
    record Success(String message) implements ValidationResult {}
    record Failure(List<String> errors) implements ValidationResult {}
}
```

### 3. **Make Illegal States Unrepresentable**
- Sealed interfaces prevent invalid states
- Enum constraints for valid values
- Constructor validation prevents invalid objects
- Precise type modeling

```java
public enum HolidayType {
    NATIONAL, STATE, MUNICIPAL, RELIGIOUS, COMMERCIAL;
    
    public boolean isGovernmental() {
        return this == NATIONAL || this == STATE || this == MUNICIPAL;
    }
}

// Using sealed interfaces to prevent invalid combinations
public sealed interface ValidationResult 
    permits ValidationResult.Success, ValidationResult.Failure {
    
    record Success(String message) implements ValidationResult {}
    record Failure(List<String> errors) implements ValidationResult {}
}
```

### 4. **Separate Operations from Data**
- Operations are implemented in service classes, not on records
- Pattern matching with `switch` for type-safe operations
- Clear separation between data and behavior
- Operations work with immutable data

```java
@Service
public class HolidayOperations {
    
    public ValidationResult validateHoliday(Holiday holiday) {
        return switch (holiday.type()) {
            case NATIONAL -> validateNationalHoliday(holiday);
            case STATE -> validateStateHoliday(holiday);
            case MUNICIPAL -> validateMunicipalHoliday(holiday);
            case RELIGIOUS -> validateReligiousHoliday(holiday);
            case COMMERCIAL -> validateCommercialHoliday(holiday);
        };
    }
    
    public String formatHolidayInfo(Holiday holiday) {
        return switch (holiday.location()) {
            case Location(var country, Optional.empty(), Optional.empty()) -> 
                "%s - National holiday in %s".formatted(holiday.name(), country);
            case Location(var country, Optional<String> state, Optional.empty()) when state.isPresent() -> 
                "%s - State holiday in %s, %s".formatted(holiday.name(), state.get(), country);
            case Location(var country, Optional<String> state, Optional<String> city) 
                when state.isPresent() && city.isPresent() -> 
                "%s - Municipal holiday in %s, %s, %s".formatted(holiday.name(), city.get(), state.get(), country);
            default -> "%s - Holiday".formatted(holiday.name());
        };
    }
}
```

## 🏗️ Architecture

The project follows a clean, data-oriented architecture with Spring Boot 3:

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│  REST Controller│───▶│   Validation     │───▶│     Mapping     │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                │                        │
                                ▼                        ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│    MongoDB      │◀───│     Service      │◀───│  Domain Objects │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                │                        │
                                ▼                        ▼
                       ┌──────────────────┐    ┌─────────────────┐
                       │   Repository     │    │   Operations    │
                       └──────────────────┘    └─────────────────┘
```

## 🚀 Quick Start

### 📮 **Testing with Postman (Recommended)**

**Fastest way to explore the API!** No setup required.

1. **Import Collections**: Use the ready-made Postman collections in [`postman/`](./postman/)
2. **Start API**: Run `make run` to start the application
3. **Test Everything**: 23 pre-configured requests covering all DOP patterns

👉 **[Complete Postman Guide](./postman/README.md)**

### 📚 For Students (No Java Installation Required)

**Perfect for learning and study purposes!** You don't need Java 24 installed locally.

```bash
# Clone the repository
git clone https://github.com/vagnerclementino/odp-api-holiday.git
cd odp-api-holiday

# Start the complete application (builds inside Docker)
make run

# Or run in background
make run-detached

# Check status
make status

# Access the API
curl http://localhost:8080/api/holidays
```

The application will be built inside Docker using Java 24, so you only need:
- **Docker & Docker Compose** 
- **Make** (optional, for convenience commands)

### 🛠️ For Developers (Local Java Development)

If you want to develop with local Java tools:

### Prerequisites
- **Java 24** (Amazon Corretto 24 recommended)
- Maven 3.9.11+
- Docker & Docker Compose
- Make (optional, for convenience commands)

### Setup and Run

1. **Clone the repository:**
   ```bash
   git clone https://github.com/vagnerclementino/odp-api-holiday.git
   cd odp-api-holiday
   ```

2. **Complete setup (one command):**
   ```bash
   make setup
   ```
   This will:
   - Start MongoDB 8 container
   - Build the Spring Boot application with Java 24
   - Start the REST API server
   - Display the API URL

3. **Test the API:**
   ```bash
   make test
   ```

### Java 24 Configuration

This project is configured by default to run with **Java 24 (Amazon Corretto)** and **Spring Boot 3.5.4**:

✅ **Default Java 24 Support**: No special profiles or scripts needed
✅ **Preview Features**: Automatically enabled via Maven configuration
✅ **Spring Boot 3.5.4**: Latest version with enhanced Java 24 support
✅ **Optimized Configuration**: Jackson, MongoDB, and Actuator configured for Java 24

**Technical Details:**
- **Runtime**: Java 24 (Amazon Corretto) with preview features enabled
- **Compilation**: Java 24 (full Java 24 support)
- **Spring Boot**: 3.5.4 with Spring Framework 6.2.9
- **Build Tool**: Maven 3.9.11

### Development Commands

| Command | Description | Java Required |
|---------|-------------|---------------|
| `make run` | Build and run from source (Docker) | ❌ No |
| `make dev` | Start development mode (Docker) | ❌ No |
| `make run-local` | Build and run with local Java | ✅ Java 24 |
| `make dev-local` | Development mode with local Java | ✅ Java 24 |
| `make run-detached` | Run in background (Docker) | ❌ No |
| `make infra` | Start only MongoDB | ❌ No |
| `make db` | Start only MongoDB database | ❌ No |
| `make test` | Run integration tests | ❌ No |
| `make unit-test` | Run unit tests | ❌ No |
| `make quality` | Run complete quality workflow | ❌ No |
| `make checkstyle` | Run Checkstyle analysis | ❌ No |
| `make checkstyle-fix` | Auto-fix style violations | ❌ No |
| `make reports` | Generate and open HTML reports | ❌ No |

### Manual Setup (if Make is not available)

1. **Start MongoDB:**
   ```bash
   docker-compose up -d mongodb
   ```

2. **Build the application:**
   ```bash
   ./mvnw clean package -DskipTests
   ```

3. **Run the Spring Boot application:**
   ```bash
   java -jar target/holiday-api-*.jar
   ```

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
    "name": "New Year",
    "date": "2024-01-01",
    "country": "Brazil",
    "state": "SP",
    "city": "São Paulo",
    "type": "NATIONAL",
    "recurring": true,
    "description": "New Year celebration"
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
    "recurring": true,
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
- **Java 24**: Version compatibility
- **Spring Boot 3.5.4**: Framework version
- **MongoDB 8**: Database version

### 🚀 **Quality Workflow**

The `quality.yml` workflow ensures code quality through:

| Stage | Description | Tools |
|-------|-------------|-------|
| **🏗️ Build** | Compile source and test code, create JAR | Maven, Java 24 |
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

---

---

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
- **Spring Boot 3.5.4**: Modern Java web framework
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
5. **How to build REST APIs** with Spring Boot 3 and modern Java
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

## 📖 Related Article

This project accompanies the article **"Nem tudo é objeto: Programação Orientada a Dados em Java"** which provides detailed explanations of the concepts demonstrated here.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
- Add more examples of DOP principles
- Enhance the documentation

## 📖 Related Article

This project accompanies the article **"Nem tudo é objeto: Programação Orientada a Dados em Java"** which provides detailed explanations of the concepts demonstrated here.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.