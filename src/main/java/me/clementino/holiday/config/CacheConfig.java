package me.clementino.holiday.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for holiday calculations. Enables Spring Cache abstraction for caching
 * calculated holidays by year.
 */
@Configuration
@EnableCaching
public class CacheConfig {
  // Cache configuration is handled through application.yml
  // Cache names: holidays-by-year, calculated-holidays, locality-holidays
}
