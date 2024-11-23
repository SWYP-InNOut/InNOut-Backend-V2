package com.example.inandout.api.presentation.healthcheck;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HealthCheckController {
    @GetMapping("/healthcheck")
    public String testConnection() {
        log.info("testConnection");
        return "SUCCESS";
    }
}
