package org.example.adventuretime.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Optional;
import org.example.adventuretime.dto.TransportDto;
import org.example.adventuretime.exception.ValidationException;
import org.example.adventuretime.service.TransportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class TransportController {

    private static final Logger logger = LoggerFactory.getLogger(TransportController.class);
    private final TransportService transportService;

    public TransportController(TransportService transportService) {
        this.transportService = transportService;
    }

    @Operation(summary = "Get all transports")
    @GetMapping("/transport")
    public List<TransportDto> getAllTransports() {
        return transportService.findAll();
    }

    @Operation(summary = "Get transport by ID")
    @GetMapping("/transport/{id}")
    public ResponseEntity<TransportDto> getTransportById(@PathVariable Long id) {
        Optional<TransportDto> transport = transportService.findById(id);
        if (transport.isPresent()) {
            logger.info("Transport found: {}", transport.get());
            return ResponseEntity.ok(transport.get());
        } else {
            logger.warn("Transport not found for id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create a new transport")
    @PostMapping("/transport")
    public ResponseEntity<TransportDto> createTransport(@RequestBody TransportDto transportDto) {
        if (transportDto.getName() == null || transportDto.getName().isEmpty()) {
            throw new ValidationException("Transport name is required");
        }
        if (transportDto.getCapacity() == null || transportDto.getCapacity() <= 0) {
            throw new ValidationException("Capacity must be a positive integer");
        }
        if (transportDto.getCost() == null || transportDto.getCost() < 0) {
            throw new ValidationException("Cost must be a non-negative number");
        }
        TransportDto savedTransport = transportService.save(transportDto);
        return ResponseEntity.ok(savedTransport);
    }

    @Operation(summary = "Update transport by ID")
    @PutMapping("/transport/{id}")
    public ResponseEntity<TransportDto> updateTransport(@PathVariable Long id,
                                                        @RequestBody TransportDto transportDto) {
        if (transportDto.getName() == null || transportDto.getName().isEmpty()) {
            throw new ValidationException("Transport name is required");
        }
        if (transportDto.getCapacity() == null || transportDto.getCapacity() <= 0) {
            throw new ValidationException("Capacity must be a positive integer");
        }
        if (transportDto.getCost() == null || transportDto.getCost() < 0) {
            throw new ValidationException("Cost must be a non-negative number");
        }
        TransportDto updatedTransport = transportService.updateTransport(id, transportDto);
        return ResponseEntity.ok(updatedTransport);
    }

    @Operation(summary = "Delete transport by ID")
    @DeleteMapping("/transport/{id}")
    public ResponseEntity<Void> deleteTransport(@PathVariable Long id) {
        transportService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
