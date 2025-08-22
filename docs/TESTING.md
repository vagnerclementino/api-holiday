# Testing Guide

This document describes the testing strategy and how to run different types of tests in the Holiday API project.

## Test Categories

The project uses JUnit 5 tags to categorize tests into two main types:

### 🔵 Unit Tests (`@Tag("unit")`)

Unit tests are fast, isolated tests that don't require external dependencies like databases, web servers, or file systems. They test individual components in isolation using mocks and stubs when necessary.

**Characteristics:**
- ⚡ Fast execution (< 1 second per test)
- 🔒 Isolated (no external dependencies)
- 🎯 Focused (test single units of code)
- 🔄 Repeatable (same result every time)

**Examples:**
- Domain object tests (Holiday, Location, etc.)
- DTO validation tests
- Mapper tests with mock data
- Utility class tests

### 🟢 Integration Tests (`@Tag("integration")`)

Integration tests verify that different components work together correctly. They may use real databases, web servers, or other external systems.

**Characteristics:**
- 🐳 Uses TestContainers for real MongoDB instances
- 🌐 Tests full application context
- 📊 Verifies end-to-end functionality
- ⏱️ Slower execution (several seconds per test)

**Examples:**
- Spring Boot context loading tests
- Database integration tests
- API endpoint tests
- Service layer integration tests

## Running Tests

### Run All Tests (Default)

```bash
# Run all tests (unit + integration)
./mvnw test

# Or explicitly use the all-tests profile
./mvnw test -Pall-tests
```

### Run Only Unit Tests

```bash
# Fast execution - only unit tests
./mvnw test -Punit-tests
```

### Run Only Integration Tests

```bash
# Slower execution - only integration tests
./mvnw test -Pintegration-tests
```

### Run Specific Test Classes

```bash
# Run a specific test class
./mvnw test -Dtest=HolidayOperationsTest

# Run multiple test classes
./mvnw test -Dtest=HolidayOperationsTest,CreateHolidayRequestTest
```

### Run Tests with Specific Tags (Alternative)

```bash
# Run only unit tests using JUnit tag
./mvnw test -Dgroups=unit

# Run only integration tests using JUnit tag
./mvnw test -Dgroups=integration

# Exclude integration tests
./mvnw test -DexcludedGroups=integration
```

## CI/CD Pipeline

The GitHub Actions workflow runs tests in separate jobs for better performance and clarity:

### Pipeline Jobs

1. **🏗️ Build Application** - Compile and package
2. **🎨 Code Style Check** - Checkstyle and Spotless
3. **🧪 Unit Tests** - Run `@Tag("unit")` tests only
4. **🔗 Integration Tests** - Run `@Tag("integration")` tests only
5. **🚪 Quality Gate** - Aggregate results

### Pipeline Commands

```yaml
# Unit Tests Job
./mvnw test -Punit-tests -B -V

# Integration Tests Job  
./mvnw test -Pintegration-tests -B -V
```

## Adding New Tests

### For Unit Tests

```java
@Tag("unit")
class MyServiceTest {
    
    @Test
    void shouldDoSomething() {
        // Test implementation
    }
}
```

### For Integration Tests

```java
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Tag("integration")
class MyIntegrationTest {
    
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");
    
    @Test
    void shouldIntegrateCorrectly() {
        // Integration test implementation
    }
}
```

## Test Configuration

### Maven Profiles

The project defines three Maven profiles in `pom.xml`:

- **`all-tests`** (default) - Runs all tests
- **`unit-tests`** - Runs only `@Tag("unit")` tests
- **`integration-tests`** - Runs only `@Tag("integration")` tests

### Surefire Configuration

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>--enable-preview</argLine>
        <groups>${test.groups}</groups>
        <excludedGroups>${test.excludedGroups}</excludedGroups>
    </configuration>
</plugin>
```

## Best Practices

### Unit Tests
- ✅ Use `@Tag("unit")` annotation
- ✅ Mock external dependencies
- ✅ Test single units of functionality
- ✅ Keep tests fast (< 1 second)
- ✅ Use descriptive test names
- ✅ Follow AAA pattern (Arrange, Act, Assert)

### Integration Tests
- ✅ Use `@Tag("integration")` annotation
- ✅ Use TestContainers for real databases
- ✅ Test component interactions
- ✅ Use `@SpringBootTest` for full context
- ✅ Clean up resources after tests
- ✅ Use realistic test data

### General Guidelines
- 📝 Write tests before or alongside code (TDD/BDD)
- 🎯 Aim for high test coverage (>80%)
- 🔄 Keep tests independent and repeatable
- 📚 Document complex test scenarios
- 🚀 Run tests frequently during development

## Troubleshooting

### Common Issues

**Docker not available for integration tests:**
```bash
# Integration tests require Docker for TestContainers
# Install Docker Desktop or ensure Docker daemon is running
docker --version
```

**Tests not running with tags:**
```bash
# Verify tags are properly applied
./mvnw test -Dgroups=unit -Dtest=MyTest -X
```

**Maven profile not working:**
```bash
# Check active profiles
./mvnw help:active-profiles
```

### Performance Tips

- Run unit tests frequently during development
- Run integration tests before commits/pushes
- Use IDE test runners for quick feedback
- Leverage Maven test caching for faster builds

## Test Reports

Test reports are generated in:
- `target/surefire-reports/` - XML and TXT reports
- `target/site/surefire-report.html` - HTML report

In CI/CD, reports are uploaded as artifacts and published via test-reporter actions.
