package com.example.inandout;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class InandoutV2Application {

    public static void main(String[] args) {
        SpringApplication.run(InandoutV2Application.class, args);
        log.info("hello inandout");
    }

}
