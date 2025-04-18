package org.example.adventuretime.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.adventuretime.logging.VisitCounter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visits")
public class VisitCounterController {

    private final VisitCounter visitCounter;

    public VisitCounterController(VisitCounter visitCounter) {
        this.visitCounter = visitCounter;
    }

    @Operation(summary = "Get the number of visits for a specific URL")
    @GetMapping
    public ResponseEntity<Long> getVisitCount(@RequestParam String url) {
        long count = visitCounter.getVisitCount(url);
        return ResponseEntity.ok(count);
    }
}