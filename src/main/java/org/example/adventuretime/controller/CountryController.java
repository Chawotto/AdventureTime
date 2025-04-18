package org.example.adventuretime.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.example.adventuretime.dto.CountryDto;
import org.example.adventuretime.dto.ResponseDto;
import org.example.adventuretime.exception.ValidationException;
import org.example.adventuretime.service.CountryService;
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
public class CountryController {

    private static final Logger logger = LoggerFactory.getLogger(CountryController.class);
    public static final String ATTRACTIONS = "Attractions are required";
    public static final String VISA = "Visa cost must be a non-negative number";
    public static final String LANGUAGES_ARE_REQUIRED = "National languages are required";
    private final CountryService countryService;
    private static final String COUNTRY_NAME_REQUIRED = "Country name is required";

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @Operation(summary = "Get all countries")
    @GetMapping("/countries")
    public List<CountryDto> getAllCountries() {
        return countryService.findAll();
    }

    @Operation(summary = "Get country by ID")
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

    @Operation(summary = "Create a new country")
    @PostMapping("/countries")
    public ResponseEntity<CountryDto> createCountry(@RequestBody CountryDto countryDto) {
        nameException(countryDto);
        CountryDto savedCountry = countryService.save(countryDto);
        return ResponseEntity.ok(savedCountry);
    }

    private void nameException(@RequestBody CountryDto countryDto) {
        if (countryDto.getName() == null || countryDto.getName().trim().isEmpty()) {
            throw new ValidationException(COUNTRY_NAME_REQUIRED);
        }
        if (countryDto.getAttractions() == null || countryDto.getAttractions().trim().isEmpty()) {
            throw new ValidationException(ATTRACTIONS);
        }
        if (countryDto.getVisaCost() == null || countryDto.getVisaCost() < 0) {
            throw new ValidationException(VISA);
        }
        if (countryDto.getNationalLanguages()
                == null || countryDto.getNationalLanguages().trim().isEmpty()) {
            throw new ValidationException(LANGUAGES_ARE_REQUIRED);
        }
    }

    @Operation(summary = "Update country by ID")
    @PutMapping("/countries/{id}")
    public ResponseEntity<CountryDto> updateCountry(@PathVariable Long id,
                                                    @RequestBody CountryDto countryDto) {
        nameException(countryDto);
        CountryDto updatedCountry = countryService.updateCountry(id, countryDto);
        return ResponseEntity.ok(updatedCountry);
    }

    @Operation(summary = "Delete country by ID")
    @DeleteMapping("/countries/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Long id) {
        countryService.deleteCountry(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add country to tour")
    @PostMapping("/countries/{countryId}/tours/{tourId}")
    @Transactional
    public ResponseEntity<CountryDto> addTourToCountry(@PathVariable Long countryId,
                                                       @PathVariable Long tourId) {
        CountryDto updatedCountry = countryService.addTourToCountry(countryId, tourId);
        logger.info("Tour added to country: Tour ID = {}, Country ID = {}", tourId, countryId);
        return ResponseEntity.ok(updatedCountry);
    }

    @Operation(summary = "Delete country from tour")
    @DeleteMapping("/countries/{countryId}/tours/{tourId}")
    @Transactional
    public ResponseEntity<CountryDto> removeTourFromCountry(@PathVariable Long countryId,
                                                            @PathVariable Long tourId) {
        CountryDto updatedCountry = countryService.removeTourFromCountry(countryId, tourId);
        logger.info("Tour removed from country: Tour ID = {}, Country ID = {}", tourId, countryId);
        return ResponseEntity.ok(updatedCountry);
    }

    @Operation(summary = "Check country availability (query)")
    @GetMapping("/query")
    public ResponseDto getQueryParams(@RequestParam String country) {
        if (country == null || country.trim().isEmpty()) {
            throw new ValidationException(COUNTRY_NAME_REQUIRED);
        }
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

    @Operation(summary = "Create multiple countries")
    @PostMapping("/countries/bulk")
    public ResponseEntity<List<CountryDto>> createCountries(@RequestBody
                                                                List<CountryDto> countryDtos) {
        countryDtos.forEach(this::nameException);

        List<CountryDto> savedCountries = countryDtos.stream()
                .map(countryService::save)
                .toList();

        return ResponseEntity.ok(savedCountries);
    }

    @Operation(summary = "Country info (path)")
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
