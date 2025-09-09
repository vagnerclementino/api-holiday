package me.clementino.holiday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class HolidayApiApplication {

  public static void main(final String[] args) {
    SpringApplication.run(HolidayApiApplication.class, args);
  }
}
