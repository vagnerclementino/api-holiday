package me.clementino.holiday.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Home controller providing basic API information.
 */
@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
            "message", "Welcome to Holiday API",
            "description", "Data-Oriented Programming Holiday API",
            "swagger", "/swagger-ui.html",
            "api-docs", "/api-docs"
        );
    }
}
