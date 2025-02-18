package org.example.adventuretime;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.example.adventuretime.dao.CountryDao;
import org.example.adventuretime.dao.TourDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    private final CountryDao countryDao;
    private final TourDao tourDao;

    public Controller(CountryDao countryDao, TourDao tourDao) {
        this.countryDao = countryDao;
        this.tourDao = tourDao;
    }

    @GetMapping("/countries")
    public List<Country> getAllCountries() {
        return countryDao.findAll();
    }

    @GetMapping("/countries/{id}")
    public ResponseEntity<Country> getCountryById(@PathVariable Long id) {
        Optional<Country> country = countryDao.findById(id);
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
        Country savedCountry = countryDao.save(country);
        return ResponseEntity.ok(savedCountry);
    }

    @PostMapping("/countries-with-tours")
    public ResponseEntity<Country> createCountryWithTours(@RequestBody Country country,
                                                          @RequestParam List<Long> tourIds) {
        List<Tour> tours = tourDao.findAll().stream()
                .filter(tour -> tourIds.contains(tour.getId()))
                .toList();
        country.setTours(new HashSet<>(tours));
        Country savedCountry = countryDao.save(country);
        return ResponseEntity.ok(savedCountry);
    }

    @PutMapping("/countries/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable Long id,
                                                 @RequestBody Country countryDetails) {
        Country country = countryDao.findById(id).orElseThrow();
        country.setName(countryDetails.getName());
        country.setAvailable(countryDetails.isAvailable());
        Country updatedCountry = countryDao.save(country);
        return ResponseEntity.ok(updatedCountry);
    }

    @DeleteMapping("/countries/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Long id) {
        countryDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/countries/{countryId}/tours/{tourId}")
    @Transactional
    public ResponseEntity<Void> addTourToCountry(@PathVariable Long countryId,
                                                 @PathVariable Long tourId) {
        Country country = countryDao.findById(countryId).orElseThrow();
        Tour tour = tourDao.findById(tourId).orElseThrow();
        country.getTours().add(tour);
        tour.getCountries().add(country);
        countryDao.save(country);
        tourDao.save(tour);
        logger.info("Tour added to country: Tour ID = {}, Country ID = {}", tourId, countryId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/countries/{countryId}/tours/{tourId}")
    @Transactional
    public ResponseEntity<Void> removeTourFromCountry(@PathVariable Long countryId,
                                                      @PathVariable Long tourId) {
        Country country = countryDao.findById(countryId).orElseThrow();
        Tour tour = tourDao.findById(tourId).orElseThrow();
        country.getTours().remove(tour);
        tour.getCountries().remove(country);
        countryDao.save(country);
        tourDao.save(tour);
        logger.info("Tour removed from country: Tour ID = {}, Country ID = {}", tourId, countryId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tours")
    public List<Tour> getAllTours() {
        return tourDao.findAll();
    }

    @GetMapping("/tours/{id}")
    public ResponseEntity<Tour> getTourById(@PathVariable Long id) {
        Optional<Tour> tour = tourDao.findById(id);
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
        Tour savedTour = tourDao.save(tour);
        return ResponseEntity.ok(savedTour);
    }

    @PutMapping("/tours/{id}")
    public ResponseEntity<Tour> updateTour(@PathVariable Long id, @RequestBody Tour tourDetails) {
        Tour tour = tourDao.findById(id).orElseThrow();
        tour.setName(tourDetails.getName());
        Tour updatedTour = tourDao.save(tour);
        return ResponseEntity.ok(updatedTour);
    }

    @DeleteMapping("/tours/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/query")
    public ResponseDto getQueryParams(@RequestParam String country) {
        Optional<Country> availableCountry = countryDao.findByName(country);
        if (availableCountry.isPresent()) {
            String availability = availableCountry.get().isAvailable() ? "is available." :
                    "is not available.";
            return new ResponseDto("Country " + country + " " + availability);
        }
        return new ResponseDto("Country " + country + " is not found.");
    }

    @GetMapping("/path/{country}")
    public ResponseDto getPathParams(@PathVariable String country) {
        Optional<Country> availableCountry = countryDao.findByName(country);
        if (availableCountry.isPresent()) {
            String status = availableCountry.get().isAvailable() ? "available" : "not available";
            return new ResponseDto(String.format("The country %s is %s.", country, status));
        }
        return new ResponseDto(String.format("The country %s is not found.", country));
    }
}