# Multi-stage Dockerfile for Holiday API with Java 24
# This Dockerfile expects the JAR to be built locally with 'make build-artifact'
# Using Amazon Corretto 24 - AWS's no-cost, multiplatform, production-ready distribution of OpenJDK

# Stage 1: Preparation stage
FROM amazoncorretto:24 AS builder

# Update packages and install necessary utilities (curl-minimal is already available)
RUN yum update -y && \
    yum install -y ca-certificates shadow-utils && \
    yum clean all

# Create app user and group
RUN groupadd -r app && useradd -r -g app app

# Set working directory
WORKDIR /home/app

# Copy the pre-built JAR from local target directory
COPY target/holiday-api-*.jar app.jar

# Verify the JAR file and extract layers for better caching
RUN java -Djarmode=layertools -jar app.jar list

# Stage 2: Runtime stage (optimized)
FROM amazoncorretto:24

# Metadata
LABEL maintainer="Holiday API Team"
LABEL description="Holiday API with Java 24 (Amazon Corretto) and Spring Boot 3.5.4"
LABEL version="1.0"

# Update packages and install necessary utilities (curl-minimal is already available)
RUN yum update -y && \
    yum install -y ca-certificates shadow-utils && \
    yum clean all

# Create app user and group (non-root for security)
RUN groupadd -r app && useradd -r -g app app

# Set working directory
WORKDIR /home/app

# Copy the JAR from builder stage
COPY --from=builder /home/app/app.jar app.jar

# Change ownership to app user
RUN chown -R app:app /home/app

# Switch to non-root user for security
USER app

# Expose ports
EXPOSE 8080 5005

# Add health check (using curl-minimal which is already available)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Set JVM options for optimal performance
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

# Run the application with Java 24 preview features and optimized JVM settings
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS --enable-preview -jar app.jar"]
