package org.example.adventuretime.controller;

import java.util.List;
import java.util.Optional;
import org.example.adventuretime.dto.CountryDto;
import org.example.adventuretime.dto.ResponseDto;
import org.example.adventuretime.service.CountryService;
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

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/countries")
    public List<CountryDto> getAllCountries() {
        return countryService.findAll();
    }

    @GetMapping("/countries/{id}")
    public ResponseEntity<CountryDto> getCountryById(@PathVariable Long id) {
        Optional<CountryDto> country = countryService.findById(id);
        if (country.isPresent()) {
            logger.info("Country found: {}", country.get());
            return ResponseEntity.ok(country.get());
        } else {
            logger.warn("Country not found for id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/countries")
    public ResponseEntity<CountryDto> createCountry(@RequestBody CountryDto countryDto) {
        CountryDto savedCountry = countryService.save(countryDto);
        return ResponseEntity.ok(savedCountry);
    }

    @PostMapping("/countries-with-tours")
    public ResponseEntity<CountryDto> createCountryWithTours(@RequestBody CountryDto countryDto,
                                                             @RequestParam List<Long> tourIds) {
        CountryDto savedCountry = countryService.createCountryWithTours(countryDto, tourIds);
        return ResponseEntity.ok(savedCountry);
    }

    @PutMapping("/countries/{id}")
    public ResponseEntity<CountryDto> updateCountry(@PathVariable Long id,
                                                    @RequestBody CountryDto countryDto) {
        CountryDto updatedCountry = countryService.updateCountry(id, countryDto);
        return ResponseEntity.ok(updatedCountry);
    }

    @DeleteMapping("/countries/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Long id) {
        countryService.deleteCountry(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/countries/{countryId}/tours/{tourId}")
    @Transactional
    public ResponseEntity<CountryDto> addTourToCountry(@PathVariable Long countryId,
                                                       @PathVariable Long tourId) {
        CountryDto updatedCountry = countryService.addTourToCountry(countryId, tourId);
        logger.info("Tour added to country: Tour ID = {}, Country ID = {}", tourId, countryId);
        return ResponseEntity.ok(updatedCountry);
    }

    @DeleteMapping("/countries/{countryId}/tours/{tourId}")
    @Transactional
    public ResponseEntity<CountryDto> removeTourFromCountry(@PathVariable Long countryId,
                                                            @PathVariable Long tourId) {
        CountryDto updatedCountry = countryService.removeTourFromCountry(countryId, tourId);
        logger.info("Tour removed from country: Tour ID = {}, Country ID = {}", tourId, countryId);
        return ResponseEntity.ok(updatedCountry);
    }

    @GetMapping("/query")
    public ResponseDto getQueryParams(@RequestParam String country) {
        String searchPattern = country + "%";
        List<CountryDto> availableCountries = countryService.findByNameLike(searchPattern);

        if (!availableCountries.isEmpty()) {
            StringBuilder responseMessage = new StringBuilder("Countries found: ");
            for (CountryDto c : availableCountries) {
                String availability = c.isAvailable() ? "is available." : "is not available.";
                responseMessage.append(c.getName()).append(" ").append(availability).append(" ");
            }
            return new ResponseDto(responseMessage.toString());
        }

        return new ResponseDto("Country " + country + " is not found.");
    }

    @GetMapping("/path/{id}")
    public ResponseDto getPathParams(@PathVariable Long id) {
        Optional<CountryDto> availableCountry = countryService.findById(id);
        if (availableCountry.isPresent()) {
            String status = availableCountry.get().isAvailable() ? "available" : "not available";
            String countryName = availableCountry.get().getName();
            return new ResponseDto(String.format("The country %s is %s.", countryName, status));
        }
        return new ResponseDto(String.format("The country with ID %d is not found.", id));
    }
}
