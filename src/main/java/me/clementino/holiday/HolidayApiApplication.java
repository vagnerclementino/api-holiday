package me.clementino.holiday;

import me.clementino.holiday.config.HolidayApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(HolidayApiProperties.class)
public class HolidayApiApplication {

  public static void main(final String[] args) {
    SpringApplication.run(HolidayApiApplication.class, args);
  }
}
