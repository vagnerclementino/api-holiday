package me.clementino.holiday.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Swagger/OpenAPI configuration. */
@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Holiday API")
                .description(
                    "Data-Oriented Programming Holiday API - A comprehensive example demonstrating DOP principles in Java")
                .version("1.0.0")
                .contact(
                    new Contact()
                        .name("Vagner Clementino")
                        .url("https://github.com/vagnerclementino")
                        .email("vagner.clementinogmail.com"))
                .license(
                    new License().name("MIT License").url("https://opensource.org/licenses/MIT")));
  }
}
