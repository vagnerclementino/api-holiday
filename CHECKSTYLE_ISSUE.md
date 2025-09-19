# Issue: Re-enable Code Quality Tools when Java 25 Module Import Declarations are supported

## Description

Both Checkstyle and Google Java Format (fmt-maven-plugin) were temporarily disabled in the Maven build process because they don't yet support Java 25 Module Import Declarations syntax (`import module <module-name>;`).

## Current Status

- **Checkstyle execution** is commented out in `pom.xml`
- **Google Java Format (fmt-maven-plugin) execution** is commented out in `pom.xml`
- The project uses Java 25 Module Import Declarations throughout the codebase
- Code quality is maintained through JaCoCo for test coverage

## Tools Affected

1. **Checkstyle** - Static code analysis
2. **Google Java Format** - Code formatting
3. **Spotless** - May also need updates

## Action Required

Monitor tool releases and re-enable when support is added:

### For Checkstyle

1. **Check Checkstyle releases** for Java 25 Module Import Declarations support
2. **Update Checkstyle version** in `pom.xml` when support is available
3. **Uncomment the execution block** in the maven-checkstyle-plugin configuration
4. **Run tests** to ensure compatibility

### For Google Java Format

1. **Check Google Java Format releases** for Java 25 Module Import Declarations support
2. **Update fmt-maven-plugin version** in `pom.xml` when support is available
3. **Uncomment the execution block** in the fmt-maven-plugin configuration
4. **Run formatting** to ensure compatibility

## Files Modified

- `pom.xml`:
  - Commented out checkstyle execution block (lines ~226-234)
  - Commented out fmt-maven-plugin execution block (lines ~250-258)

## Related Links

- [JEP 511: Module Import Declarations (Preview)](https://openjdk.org/jeps/511)
- [JEP 512: Module Import Declarations (Second Preview)](https://openjdk.org/jeps/512)
- [Checkstyle GitHub Repository](https://github.com/checkstyle/checkstyle)
- [Google Java Format GitHub Repository](https://github.com/google/google-java-format)

## Expected Timeline

This should be resolved when:

- Tools release support for Java 25 preview features
- Java 25 Module Import Declarations become a standard feature (not preview)

## Priority

Medium - Code quality is maintained through manual review and JaCoCo, but these tools provide additional valuable checks and formatting.
