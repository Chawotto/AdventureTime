package org.example.adventuretime.controllers;

import java.util.List;
import java.util.Optional;
import org.example.adventuretime.model.Tour;
import org.example.adventuretime.service.TourService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TourController {

    private static final Logger logger = LoggerFactory.getLogger(TourController.class);

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping("/tours")
    public List<Tour> getAllTours() {
        return tourService.findAll();
    }

    @GetMapping("/tours/{id}")
    public ResponseEntity<Tour> getTourById(@PathVariable Long id) {
        Optional<Tour> tour = tourService.findById(id);
        if (tour.isPresent()) {
            logger.info("Tour found: {}", tour.get());
            return ResponseEntity.ok(tour.get());
        } else {
            logger.warn("Tour not found for id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/tours")
    public ResponseEntity<Tour> createTour(@RequestBody Tour tour) {
        Tour savedTour = tourService.save(tour);
        return ResponseEntity.ok(savedTour);
    }

    @PutMapping("/tours/{id}")
    public ResponseEntity<Tour> updateTour(@PathVariable Long id, @RequestBody Tour tourDetails) {
        Tour tour = tourService.findById(id).orElseThrow();
        tour.setName(tourDetails.getName());
        Tour updatedTour = tourService.save(tour);
        return ResponseEntity.ok(updatedTour);
    }

    @DeleteMapping("/tours/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
