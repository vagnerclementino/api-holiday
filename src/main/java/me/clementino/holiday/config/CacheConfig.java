package me.clementino.holiday.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for holiday calculations.
 *
 * <p>This configuration enables caching for year-based holiday calculations to improve performance
 * when the same calculations are requested multiple times.
 */
@Configuration
@EnableCaching
public class CacheConfig {

  /**
   * Configure cache manager for holiday calculations.
   *
   * @return configured cache manager
   */
  @Bean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager(
        "yearHolidays", // Cache for holidays by year
        "yearLocationHolidays", // Cache for holidays by year and location
        "yearTypeHolidays" // Cache for holidays by year and type
        );
  }
}
