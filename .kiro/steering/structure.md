# Project Structure & Organization

## Data-Oriented Programming Architecture

The project follows DOP principles with clear separation between data and operations:

```
src/main/java/me/clementino/holiday/
├── domain/                    # Core domain data structures
│   ├── Holiday.java          # MongoDB entity (mutable for persistence)
│   ├── HolidayData.java      # Immutable domain record
│   ├── Location.java         # Location record (country/state/city)
│   ├── HolidayType.java      # Enum for holiday categories
│   ├── HolidayStatus.java    # Sealed interface for status variants
│   ├── ValidationResult.java # Sealed interface for validation outcomes
│   ├── HolidayQuery.java     # Query parameters record
│   ├── HolidayCommand.java   # Command pattern records
│   ├── HolidayFilter.java    # Filtering criteria record
│   ├── dop/                  # Advanced DOP examples
│   │   ├── Holiday.java      # Sealed interface with variants
│   │   ├── FixedHoliday.java # Fixed date holiday record
│   │   ├── MoveableHoliday.java # Calculated holiday record
│   │   ├── Locality.java     # Hierarchical location model
│   │   └── HolidayOperations.java # Pure operations
│   └── oop/                  # OOP comparison examples
├── dto/                      # Data Transfer Objects
│   ├── CreateHolidayRequest.java
│   ├── UpdateHolidayRequest.java
│   ├── HolidayResponse.java
│   └── ErrorResponse.java
├── controller/               # REST endpoints
│   ├── HolidayController.java
│   └── HomeController.java
├── service/                  # Business logic orchestration
│   ├── HolidayService.java
│   └── exceptions/
├── operations/               # Pure operations (DOP principle)
│   └── HolidayOperations.java
├── repository/               # Data persistence
│   └── HolidayRepository.java
├── mapper/                   # Data transformations
│   └── HolidayMapper.java
├── config/                   # Spring configuration
│   ├── MongoConfig.java
│   ├── JacksonConfig.java
│   ├── SwaggerConfig.java
│   └── GlobalExceptionHandler.java
└── util/                     # Utility classes
```

## Key Architectural Patterns

### DOP Principles Implementation

1. **Immutable Data**: All domain objects are records or use immutable patterns
2. **Separated Operations**: Business logic in dedicated operation classes
3. **Type Safety**: Sealed interfaces prevent invalid states
4. **Pure Functions**: Operations are side-effect free transformations

### Naming Conventions

- **Records**: PascalCase, descriptive names (e.g., `HolidayData`, `Location`)
- **Sealed Interfaces**: Use variants as nested records (e.g., `ValidationResult.Success`)
- **Operations**: Verb-based method names (e.g., `validateHoliday`, `filterHolidays`)
- **Commands**: Action-based nested records (e.g., `HolidayCommand.Create`)

### Package Organization

- `domain/` - Core business data structures (immutable)
- `domain/dop/` - Advanced DOP examples with sealed interfaces
- `domain/oop/` - OOP comparison examples
- `operations/` - Pure business operations
- `service/` - Orchestration and persistence coordination
- `dto/` - API boundary objects
- `controller/` - HTTP endpoints

## Code Style Guidelines

- **Google Java Style** with 120 character line limit
- **No indentation validation** (flexible formatting)
- **Records preferred** over classes for data
- **Sealed interfaces** for modeling alternatives
- **Pattern matching** with switch expressions
- **Optional** for nullable values in records
