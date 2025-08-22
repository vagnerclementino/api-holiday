package me.clementino.holiday;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration test for the Holiday API application. This test verifies that the Spring Boot
 * application context loads successfully with a real MongoDB instance using TestContainers.
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class HolidayApiIntegrationTest {

  @Container
  static MongoDBContainer mongoDBContainer =
      new MongoDBContainer("mongo:8").withExposedPorts(27017);

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  /**
   * Test that the application context loads successfully with MongoDB. This is a basic smoke test
   * to ensure the application starts correctly and can connect to the database.
   * 
   * Note: This test requires Docker to be available. If Docker is not available,
   * the test will be skipped automatically by TestContainers.
   */
  @Test
  void contextLoads() {
    // This test will pass if the application context loads successfully
    // with MongoDB connection established via TestContainers
    // No additional assertions needed for this basic smoke test
  }
}
