package org.example.adventuretime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AdventureTimeApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdventureTimeApplication.class, args);
    }
}
