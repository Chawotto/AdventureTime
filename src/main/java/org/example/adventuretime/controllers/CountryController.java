package org.example.adventuretime.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.example.adventuretime.country.Country;
import org.example.adventuretime.dao.CountryDao;
import org.example.adventuretime.dao.TourDao;
import org.example.adventuretime.dto.ResponseDto;
import org.example.adventuretime.tour.Tour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CountryController {

    private static final Logger logger = LoggerFactory.getLogger(CountryController.class);

    private final CountryDao countryDao;
    private final TourDao tourDao;

    public CountryController(CountryDao countryDao, TourDao tourDao) {
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

    @GetMapping("/query")
    public ResponseDto getQueryParams(@RequestParam String country) {
        String searchPattern = country + "%";
        List<Country> availableCountries = countryDao.findByNameLike(searchPattern);

        if (!availableCountries.isEmpty()) {
            StringBuilder responseMessage = new StringBuilder("Countries found: ");
            for (Country c : availableCountries) {
                String availability = c.isAvailable() ? "is available." : "is not available.";
                responseMessage.append(c.getName()).append(" ").append(availability).append(" ");
            }
            return new ResponseDto(responseMessage.toString());
        }

        return new ResponseDto("Country " + country + " is not found.");
    }

    @GetMapping("/path/{id}")
    public ResponseDto getPathParams(@PathVariable Long id) {
        Optional<Country> availableCountry = countryDao.findById(id);
        if (availableCountry.isPresent()) {
            String status = availableCountry.get().isAvailable() ? "available" : "not available";
            String countryName = availableCountry.get().getName();
            return new ResponseDto(String.format("The country %s is %s.", countryName, status));
        }
        return new ResponseDto(String.format("The country with ID %d is not found.", id));
    }
}
