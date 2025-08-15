# Requirements Document

## Introduction

This document outlines the requirements for a comprehensive REST API that demonstrates Data-Oriented Programming (DOP) principles using Java 24, Spring Boot 3, and MongoDB. The API will manage public holidays across different geographical locations (countries, states/provinces, cities) with support for various holiday types including fixed dates, moveable holidays, and observed holidays with mondayisation rules.

The system will serve as both a functional holiday management API and an educational example of DOP principles in practice, showcasing modern Java features like records, sealed interfaces, pattern matching, and immutable data structures.

## Requirements

### Requirement 1

**User Story:** As a developer learning DOP principles, I want to see a complete REST API implementation that demonstrates all four DOP principles, so that I can understand how to apply these concepts in real-world applications.

#### Acceptance Criteria

1. WHEN the API is implemented THEN the system SHALL demonstrate all four DOP principles: Model Data Immutably and Transparently, Model the Data the Whole Data and Nothing but the Data, Make Illegal States Unrepresentable, and Separate Operations from Data
2. WHEN examining the codebase THEN the system SHALL use Java 24 records for all DTOs and immutable data structures
3. WHEN examining the domain layer THEN the system SHALL use sealed interfaces to prevent illegal states
4. WHEN examining operations THEN the system SHALL separate all business logic into pure operation classes

### Requirement 2

**User Story:** As an API consumer, I want to perform CRUD operations on holidays, so that I can manage holiday data for different geographical locations.

#### Acceptance Criteria

1. WHEN I send a POST request to create a holiday THEN the system SHALL validate the holiday data and create a new holiday record
2. WHEN I send a GET request for a specific holiday THEN the system SHALL return the holiday details including calculated dates for the current year
3. WHEN I send a PUT request to update a holiday THEN the system SHALL validate the changes and update the holiday record
4. WHEN I send a DELETE request for a holiday THEN the system SHALL remove the holiday from the system
5. WHEN I send a GET request to list holidays THEN the system SHALL return a paginated list of holidays with filtering options

### Requirement 3

**User Story:** As an API consumer, I want to query holidays by various criteria, so that I can find relevant holidays for specific locations, dates, or types.

#### Acceptance Criteria

1. WHEN I query holidays by country THEN the system SHALL return all holidays applicable to that country including national, state, and city holidays
2. WHEN I query holidays by date range THEN the system SHALL return all holidays occurring within the specified period
3. WHEN I query holidays by type THEN the system SHALL return holidays filtered by category (national, state, municipal, religious, commercial)
4. WHEN I query holidays by locality THEN the system SHALL apply hierarchical matching rules (national holidays apply to all subdivisions)
5. WHEN I query holidays for a specific year THEN the system SHALL calculate the correct dates for moveable holidays

### Requirement 4

**User Story:** As an API consumer, I want to work with different types of holidays (fixed, moveable, observed), so that I can handle the complexity of real-world holiday calculations.

#### Acceptance Criteria

1. WHEN I create a fixed holiday THEN the system SHALL store holidays that occur on the same date every year
2. WHEN I create a moveable holiday THEN the system SHALL support holidays calculated using algorithms (Easter, Thanksgiving, etc.)
3. WHEN I create an observed holiday THEN the system SHALL support holidays with mondayisation rules for weekend adjustments
4. WHEN I create a derived holiday THEN the system SHALL support holidays calculated as offsets from other holidays
5. WHEN I request holiday dates for a specific year THEN the system SHALL calculate the correct dates for all holiday types

### Requirement 5

**User Story:** As an API consumer, I want comprehensive error handling and validation, so that I receive clear feedback when requests are invalid or operations fail.

#### Acceptance Criteria

1. WHEN I send invalid holiday data THEN the system SHALL return detailed validation error messages
2. WHEN I request a non-existent holiday THEN the system SHALL return a 404 Not Found response with appropriate error details
3. WHEN I send malformed requests THEN the system SHALL return 400 Bad Request with clear error descriptions
4. WHEN the system encounters internal errors THEN the system SHALL return 500 Internal Server Error without exposing sensitive information
5. WHEN validation fails THEN the system SHALL use sealed interfaces to represent different validation outcomes

### Requirement 6

**User Story:** As a developer, I want comprehensive test coverage including unit, integration, and E2E tests, so that I can ensure the system works correctly and serves as a reliable example.

#### Acceptance Criteria

1. WHEN running unit tests THEN the system SHALL have tests for all domain operations using pure functions
2. WHEN running integration tests THEN the system SHALL test repository operations with MongoDB using Testcontainers
3. WHEN running E2E tests THEN the system SHALL test complete API workflows using Spring Test
4. WHEN generating test data THEN the system SHALL use Instancio library for creating test instances
5. WHEN running all tests THEN the system SHALL achieve high code coverage across all layers

### Requirement 7

**User Story:** As a developer, I want proper data mapping between layers, so that the system maintains clean separation between DTOs, domain objects, and entities.

#### Acceptance Criteria

1. WHEN mapping between DTOs and domain objects THEN the system SHALL use MapStruct for type-safe transformations
2. WHEN persisting data THEN the system SHALL use JPA entities for MongoDB integration while keeping domain objects immutable
3. WHEN returning API responses THEN the system SHALL map domain objects to appropriate response DTOs
4. WHEN receiving API requests THEN the system SHALL validate and map request DTOs to domain objects
5. WHEN mapping fails THEN the system SHALL provide clear error messages about transformation issues

### Requirement 8

**User Story:** As a developer, I want the system to run locally using Docker, so that I can easily set up and test the application without complex environment configuration.

#### Acceptance Criteria

1. WHEN I run `make dev` THEN the system SHALL start the application and MongoDB using Docker Compose
2. WHEN I run `make test` THEN the system SHALL execute all tests in a containerized environment
3. WHEN the application starts THEN the system SHALL automatically initialize MongoDB with sample data
4. WHEN I access the API documentation THEN the system SHALL provide Swagger UI for interactive testing
5. WHEN I need to test the API THEN the system SHALL include a Postman collection with example requests

### Requirement 9

**User Story:** As an API consumer, I want proper API documentation, so that I can understand how to use all available endpoints and data structures.

#### Acceptance Criteria

1. WHEN I access the API documentation THEN the system SHALL provide comprehensive OpenAPI/Swagger documentation
2. WHEN I view endpoint documentation THEN the system SHALL include request/response examples for all operations
3. WHEN I examine data models THEN the system SHALL document all DTOs with field descriptions and validation rules
4. WHEN I need to understand holiday types THEN the system SHALL document the different holiday variants and their usage
5. WHEN I want to test the API THEN the system SHALL provide interactive documentation through Swagger UI

### Requirement 10

**User Story:** As an API consumer, I want to retrieve holidays for a specific year, so that I can get calculated holiday dates even if they haven't been previously computed and stored.

#### Acceptance Criteria

1. WHEN I request holidays for a specific year THEN the system SHALL return all applicable holidays with dates calculated for that year
2. WHEN a holiday doesn't exist in the database for the requested year THEN the system SHALL calculate the holiday date using the appropriate algorithm
3. WHEN a holiday is calculated for a new year THEN the system SHALL persist the calculated holiday instance to the database for future requests
4. WHEN I request the same year again THEN the system SHALL return the previously calculated and stored holiday instances
5. WHEN calculating moveable holidays THEN the system SHALL use the correct algorithms (Easter, Thanksgiving, etc.) for the specified year

### Requirement 11

**User Story:** As a developer, I want the system to follow Spring Boot best practices, so that it serves as a good example of modern Java web application architecture.

#### Acceptance Criteria

1. WHEN examining the architecture THEN the system SHALL use proper layered architecture with controllers, services, and repositories
2. WHEN handling configuration THEN the system SHALL use Spring Boot configuration properties and profiles
3. WHEN managing dependencies THEN the system SHALL use Spring's dependency injection appropriately
4. WHEN handling transactions THEN the system SHALL use Spring's transaction management for data operations
5. WHEN implementing security THEN the system SHALL include basic security configurations and validation
