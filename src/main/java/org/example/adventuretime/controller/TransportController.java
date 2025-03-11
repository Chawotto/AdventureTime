package org.example.adventuretime.controller;

import java.util.List;
import java.util.Optional;
import org.example.adventuretime.dto.TransportDto;
import org.example.adventuretime.exception.ValidationException;
import org.example.adventuretime.service.TransportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransportController {

    private static final Logger logger = LoggerFactory.getLogger(TransportController.class);
    private final TransportService transportService;

    public TransportController(TransportService transportService) {
        this.transportService = transportService;
    }

    @GetMapping("/transport")
    @Cacheable(value = "transport")
    public List<TransportDto> getAllTransports() {
        return transportService.findAll();
    }

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

    @PostMapping("/transport")
    @CacheEvict(value = {"transport"}, allEntries = true)
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

    @PutMapping("/transport/{id}")
    @CacheEvict(value = {"transport", "tour", "country"}, allEntries = true)
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

    @DeleteMapping("/transport/{id}")
    @CacheEvict(value = {"transport", "tour", "country"}, allEntries = true)
    public ResponseEntity<Void> deleteTransport(@PathVariable Long id) {
        transportService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
