package org.example.adventuretime.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.example.adventuretime.dto.TourDto;
import org.example.adventuretime.dto.TransportDto;
import org.example.adventuretime.exception.ValidationException;
import org.example.adventuretime.service.TourService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TourController {

    private static final Logger logger = LoggerFactory.getLogger(TourController.class);
    private final TourService tourService;
    private final TransportService transportService;

    public TourController(TourService tourService, TransportService transportService) {
        this.tourService = tourService;
        this.transportService = transportService;
    }

    @Operation(summary = "Get all tours")
    @GetMapping("/tours")
    public List<TourDto> getAllTours() {
        return tourService.findAll();
    }

    @Operation(summary = "Get tour by ID")
    @GetMapping("/tours/{id}")
    public ResponseEntity<TourDto> getTourById(@PathVariable Long id) {
        Optional<TourDto> tour = tourService.findById(id);
        if (tour.isPresent()) {
            logger.info("Tour found: {}", tour.get());
            return ResponseEntity.ok(tour.get());
        } else {
            logger.warn("Tour not found for id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create a new tour")
    @PostMapping("/tours")
    public ResponseEntity<TourDto> createTour(@RequestBody TourDto tourDto) {
        nameException(tourDto);
        TourDto savedTour = tourService.save(tourDto);
        return ResponseEntity.ok(savedTour);
    }

    private void nameException(@RequestBody TourDto tourDto) {
        if (tourDto.getName() == null || tourDto.getName().isEmpty()) {
            throw new ValidationException("Tour name is required");
        }
        if (tourDto.getDescription() == null || tourDto.getDescription().isEmpty()) {
            throw new ValidationException("Tour description is required");
        }
        if (tourDto.getDurationDays() == null || tourDto.getDurationDays() <= 0) {
            throw new ValidationException("Duration days must be a positive integer");
        }
    }

    @Operation(summary = "Update tour by ID")
    @PutMapping("/tours/{id}")
    public ResponseEntity<TourDto> updateTour(@PathVariable Long id, @RequestBody TourDto tourDto) {
        nameException(tourDto);
        TourDto updatedTour = tourService.updateTour(id, tourDto);
        return ResponseEntity.ok(updatedTour);
    }

    @Operation(summary = "Delete tour by ID")
    @DeleteMapping("/tours/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add transport to tour")
    @PostMapping("/tours/{tourId}/transport/{transportId}")
    @Transactional
    public ResponseEntity<TourDto> addTransportToTour(@PathVariable Long tourId,
                                                      @PathVariable Long transportId) {
        TransportDto transportDto = transportService.findById(transportId)
                .orElseThrow(() -> new RuntimeException("Transport not found"));
        TourDto updatedTour = tourService.addOrUpdateTransportInTour(tourId, transportDto);
        logger.info("Transport with id {} set to tour with id {}", transportId, tourId);
        return ResponseEntity.ok(updatedTour);
    }

    @Operation(summary = "Delete transport from tour")
    @DeleteMapping("/tours/{tourId}/transport")
    @Transactional
    public ResponseEntity<TourDto> removeTransportFromTour(@PathVariable Long tourId) {
        TourDto updatedTour = tourService.removeTransportFromTour(tourId);
        logger.info("Transport removed from tour with id {}", tourId);
        return ResponseEntity.ok(updatedTour);
    }

    @Operation(summary = "Sort tours by transport (JPQL)")
    @GetMapping("/tours/by-transport")
    public ResponseEntity<List<TourDto>> getToursByTransportType(@RequestParam String name) {
        if (name == null || name.isEmpty()) {
            throw new ValidationException("Transport name is required");
        }
        List<TourDto> tours = tourService.findToursByTransportType(name);
        return ResponseEntity.ok(tours);
    }

    @Operation(summary = "Sort tours by transport (Native)")
    @GetMapping("/tours/by-transportNative")
    public ResponseEntity<List<TourDto>> getToursByTransportTypeNative(@RequestParam String name) {
        if (name == null || name.isEmpty()) {
            throw new ValidationException("Transport name is required");
        }
        List<TourDto> tours = tourService.findToursByTransportTypeNative(name);
        return ResponseEntity.ok(tours);
    }
}
