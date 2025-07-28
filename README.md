# Data-Oriented Programming Holiday API

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

// Sealed interface for modeling holiday status alternatives
public sealed interface HolidayStatus 
    permits HolidayStatus.Active, HolidayStatus.Cancelled, HolidayStatus.Proposed {
    
    record Active(LocalDateTime confirmedAt) implements HolidayStatus {}
    record Cancelled(LocalDateTime cancelledAt, String reason) implements HolidayStatus {}
    record Proposed(LocalDateTime proposedAt, String proposedBy) implements HolidayStatus {}
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

### Prerequisites
- Java 17+
- Maven 3.6+
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
   - Build the Spring Boot application
   - Start the REST API server
   - Display the API URL

3. **Test the API:**
   ```bash
   make test
   ```

### Manual Setup (if Make is not available)

1. **Start MongoDB:**
   ```bash
   docker-compose up -d mongodb
   ```

2. **Build the application:**
   ```bash
   mvn clean package -DskipTests
   ```

3. **Run the Spring Boot application:**
   ```bash
   java -jar target/holiday-api-*.jar
   ```

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

### Run Unit Tests
```bash
make unit-test
# or
mvn test
```

### Run Integration Tests
```bash
make test
# or
./scripts/test-api.sh
```

The integration tests demonstrate:
- Complete data flow from API to database
- Validation at boundaries
- Immutable data transformations
- Error handling and edge cases

## 🛠️ Development Commands

| Command | Description |
|---------|-------------|
| `make setup` | Complete environment setup |
| `make build` | Build Java application |
| `make deploy` | Start MongoDB and Spring Boot |
| `make test` | Run API integration tests |
| `make unit-test` | Run unit tests |
| `make logs` | View MongoDB logs |
| `make clean` | Clean up everything |
| `make start` | Start services |
| `make stop` | Stop services |
| `make restart` | Restart services |
| `make url` | Show API URL |
| `make help` | Show all commands |

## 📁 Project Structure

```
src/
├── main/java/me/clementino/holiday/
│   ├── domain/          # Domain objects (immutable records)
│   │   ├── Holiday.java
│   │   ├── Location.java
│   │   ├── HolidayType.java
│   │   └── HolidayFilter.java
│   ├── dto/             # Data Transfer Objects
│   │   ├── CreateHolidayRequest.java
│   │   ├── HolidayResponse.java
│   │   ├── LocationResponse.java
│   │   └── ErrorResponse.java
│   ├── validator/       # Boundary validation
│   │   ├── ValidationResult.java
│   │   └── HolidayValidator.java
│   ├── mapper/          # Pure transformation functions
│   │   └── HolidayMapper.java
│   ├── service/         # Business logic
│   │   └── HolidayService.java
│   ├── repository/      # Data persistence
│   │   ├── HolidayRepository.java
│   │   └── MongoHolidayRepository.java
│   └── controller/      # REST Controllers
│       └── HolidayController.java
└── test/java/me/clementino/holiday/
    └── HolidayApiIntegrationTest.java
```

## 🏛️ Infrastructure

The project uses:
- **Spring Boot 3**: Modern Java web framework
- **MongoDB 8**: Document-oriented NoSQL database
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

## 🤝 Contributing

This project serves as an educational example. Feel free to:
- Fork and experiment with the code
- Suggest improvements via issues
- Add more examples of DOP principles
- Enhance the documentation

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