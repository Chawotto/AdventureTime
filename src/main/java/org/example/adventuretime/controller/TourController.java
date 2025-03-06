package org.example.adventuretime.controller;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.example.adventuretime.dto.TourDto;
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
    public List<TourDto> getAllTours() {
        return tourService.findAll();
    }

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

    @PostMapping("/tours")
    public ResponseEntity<TourDto> createTour(@RequestBody TourDto tourDto) {
        TourDto savedTour = tourService.save(tourDto);
        return ResponseEntity.ok(savedTour);
    }

    @PutMapping("/tours/{id}")
    public ResponseEntity<TourDto> updateTour(@PathVariable Long id, @RequestBody TourDto tourDto) {
        TourDto updatedTour = tourService.updateTour(id, tourDto);
        return ResponseEntity.ok(updatedTour);
    }

    @DeleteMapping("/tours/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tours/{tourId}/transport/{transportId}")
    @Transactional
    public ResponseEntity<TourDto> addTransportToTour(@PathVariable Long tourId,
                                                      @PathVariable Long transportId) {
        TourDto updatedTour = tourService.addOrUpdateTransportInTour(tourId, transportId);
        logger.info("Transport with id {} set to tour with id {}", transportId, tourId);
        return ResponseEntity.ok(updatedTour);
    }

    @DeleteMapping("/tours/{tourId}/transport")
    @Transactional
    public ResponseEntity<TourDto> removeTransportFromTour(@PathVariable Long tourId) {
        TourDto updatedTour = tourService.removeTransportFromTour(tourId);
        logger.info("Transport removed from tour with id {}", tourId);
        return ResponseEntity.ok(updatedTour);
    }
}
