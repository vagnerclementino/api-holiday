# Dockerfile for Holiday API
# This Dockerfile expects the JAR to be built locally with 'make build-artifact'

FROM openjdk:21-jre-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create app user
RUN groupadd -r app && useradd -r -g app app

# Set working directory
WORKDIR /home/app

# Copy the pre-built JAR from local target directory
# This JAR should be built locally using 'make build-artifact'
COPY target/holiday-api-*.jar app.jar

# Change ownership to app user
RUN chown -R app:app /home/app

# Switch to app user
USER app

# Expose port
EXPOSE 8080 5005

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
