package org.example.adventuretime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private TourRepository tourRepository;

    @GetMapping("/countries")
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    @GetMapping("/countries/{id}")
    public ResponseEntity<Country> getCountryById(@PathVariable Long id) {
        Optional<Country> country = countryRepository.findById(id);
        if (country.isPresent()) {
            logger.info("Country found: {}", country.get());
            return ResponseEntity.ok(country.get());
        } else {
            logger.warn("Country not found for id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/countries")
    public ResponseEntity<Country> createCountry(@RequestBody Country country) {
        Country savedCountry = countryRepository.save(country);
        return ResponseEntity.ok(savedCountry);
    }

    @PostMapping("/countries-with-tours")
    public ResponseEntity<Country> createCountryWithTours(@RequestBody Country country, @RequestParam List<Long> tourIds) {
        List<Tour> tours = tourRepository.findAllById(tourIds);
        country.setTours(new HashSet<>(tours));
        Country savedCountry = countryRepository.save(country);
        return ResponseEntity.ok(savedCountry);
    }

    @PutMapping("/countries/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable Long id, @RequestBody Country countryDetails) {
        Country country = countryRepository.findById(id).orElseThrow();
        country.setName(countryDetails.getName());
        country.setAvailable(countryDetails.isAvailable());
        Country updatedCountry = countryRepository.save(country);
        return ResponseEntity.ok(updatedCountry);
    }

    @DeleteMapping("/countries/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Long id) {
        countryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/countries/{countryId}/tours/{tourId}")
    @Transactional
    public ResponseEntity<Void> addTourToCountry(@PathVariable Long countryId, @PathVariable Long tourId) {
        Country country = countryRepository.findById(countryId).orElseThrow();
        Tour tour = tourRepository.findById(tourId).orElseThrow();
        country.getTours().add(tour);
        tour.getCountries().add(country);
        countryRepository.save(country);
        tourRepository.save(tour);
        logger.info("Tour added to country: Tour ID = {}, Country ID = {}", tourId, countryId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/countries/{countryId}/tours/{tourId}")
    @Transactional
    public ResponseEntity<Void> removeTourFromCountry(@PathVariable Long countryId, @PathVariable Long tourId) {
        Country country = countryRepository.findById(countryId).orElseThrow();
        Tour tour = tourRepository.findById(tourId).orElseThrow();
        country.getTours().remove(tour);
        tour.getCountries().remove(country);
        countryRepository.save(country);
        tourRepository.save(tour);
        logger.info("Tour removed from country: Tour ID = {}, Country ID = {}", tourId, countryId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tours")
    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }

    @GetMapping("/tours/{id}")
    public ResponseEntity<Tour> getTourById(@PathVariable Long id) {
        Optional<Tour> tour = tourRepository.findById(id);
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
        Tour savedTour = tourRepository.save(tour);
        return ResponseEntity.ok(savedTour);
    }

    @PutMapping("/tours/{id}")
    public ResponseEntity<Tour> updateTour(@PathVariable Long id, @RequestBody Tour tourDetails) {
        Tour tour = tourRepository.findById(id).orElseThrow();
        tour.setName(tourDetails.getName());
        Tour updatedTour = tourRepository.save(tour);
        return ResponseEntity.ok(updatedTour);
    }

    @DeleteMapping("/tours/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/query")
    public ResponseDTO getQueryParams(@RequestParam String country) {
        Country availableCountry = countryRepository.findByName(country);
        if (availableCountry != null) {
            if (availableCountry.isAvailable()) {
                return new ResponseDTO("Country " + country + " is available.");
            } else {
                return new ResponseDTO("Country " + country + " is not available.");
            }
        }
        return new ResponseDTO("Country " + country + " is not found.");
    }

    @GetMapping("/path/{country}")
    public ResponseDTO getPathParams(@PathVariable String country) {
        Country availableCountry = countryRepository.findByName(country);
        if (availableCountry != null) {
            if (availableCountry.isAvailable()) {
                return new ResponseDTO("Country " + country + " is available.");
            } else {
                return new ResponseDTO("Country " + country + " is not available.");
            }
        }
        return new ResponseDTO("Country " + country + " is not found.");
    }
}
