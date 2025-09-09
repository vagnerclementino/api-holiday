package me.clementino.holiday.controller;

import io.swagger.v3.oas.annotations.Hidden;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Home controller providing basic API information. */
@RestController
@Hidden
public class HomeController {

  @GetMapping("/")
  public Map<String, String> home() {
    return Map.of(
        "message", "Welcome to Holiday API",
        "description", "Data-Oriented Programming Holiday API",
        "swagger", "/swagger-ui.html",
        "api-docs", "/api-docs");
  }
}
