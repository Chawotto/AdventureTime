package org.example.adventuretime.service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.adventuretime.config.CacheConfig;
import org.example.adventuretime.dto.CountryDto;
import org.example.adventuretime.dto.TourDto;
import org.example.adventuretime.mapper.CountryMapper;
import org.example.adventuretime.mapper.TourMapper;
import org.example.adventuretime.model.Country;
import org.example.adventuretime.model.Tour;
import org.example.adventuretime.repository.CountryRepository;
import org.example.adventuretime.repository.TourRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryService {

    private static final String COUNTRY_NOT_FOUND = "Country not found";

    private final CountryRepository countryRepository;
    private final TourRepository tourRepository;
    private final CacheConfig cacheConfig;

    public List<CountryDto> findAll() {
        Collection<CountryDto> cachedCountries = cacheConfig.getAllCountries();
        if (!cachedCountries.isEmpty()) {
            return new ArrayList<>(cachedCountries);
        }
        List<CountryDto> countries = countryRepository.findAll().stream()
                .map(CountryMapper::toDto)
                .toList();
        countries.forEach(country -> cacheConfig.putCountry(country.getId(), country));
        return countries;
    }

    public List<CountryDto> findByNameLike(String namePattern) {
        return countryRepository.findByNameLike(namePattern).stream()
                .map(CountryMapper::toDto)
                .toList();
    }

    public Optional<CountryDto> findById(Long id) {
        CountryDto cachedCountry = cacheConfig.getCountry(id);
        if (cachedCountry != null) {
            return Optional.of(cachedCountry);
        }
        Optional<CountryDto> countryDto = countryRepository.findById(id)
                .map(CountryMapper::toDto);
        countryDto.ifPresent(country -> cacheConfig.putCountry(id, country));
        return countryDto;
    }

    public CountryDto save(CountryDto countryDto) {
        Country country = CountryMapper.toEntity(countryDto);
        Country saved = countryRepository.save(country);
        CountryDto savedDto = CountryMapper.toDto(saved);
        cacheConfig.putCountry(savedDto.getId(), savedDto);
        return savedDto;
    }

    @Transactional
    public CountryDto updateCountry(Long id, CountryDto countryDto) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(COUNTRY_NOT_FOUND));
        country.setName(countryDto.getName());
        country.setAvailable(countryDto.isAvailable());
        country.setAttractions(countryDto.getAttractions());
        country.setVisaCost(countryDto.getVisaCost());
        country.setNationalLanguages(countryDto.getNationalLanguages());
        Country updatedCountry = countryRepository.save(country);
        CountryDto updatedDto = CountryMapper.toDto(updatedCountry);

        cacheConfig.putCountry(id, updatedDto);

        for (Tour tour : country.getTours()) {
            TourDto tourDto = TourMapper.toDto(tour);
            cacheConfig.putTour(tour.getId(), tourDto);
        }

        return updatedDto;
    }

    @Transactional
    public void deleteCountry(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(COUNTRY_NOT_FOUND));
        List<Tour> toursToUpdate = new ArrayList<>(country.getTours());
        for (Tour tour : toursToUpdate) {
            tour.getCountries().remove(country);
            tourRepository.save(tour);
            TourDto tourDto = TourMapper.toDto(tour);
            cacheConfig.putTour(tour.getId(), tourDto);
        }
        countryRepository.delete(country);
        cacheConfig.removeCountry(id);
    }

    @Transactional
    public CountryDto addTourToCountry(Long countryId, Long tourId) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new RuntimeException(COUNTRY_NOT_FOUND));
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        country.getTours().add(tour);
        tour.getCountries().add(country);
        return getCountryDto(countryId, tourId, country, tour);
    }

    private CountryDto getCountryDto(Long countryId, Long tourId, Country country, Tour tour) {
        countryRepository.save(country);
        tourRepository.save(tour);

        CountryDto countryDto = CountryMapper.toDto(country);
        TourDto tourDto = TourMapper.toDto(tour);

        cacheConfig.putCountry(countryId, countryDto);
        cacheConfig.putTour(tourId, tourDto);
        return countryDto;
    }

    @Transactional
    public CountryDto removeTourFromCountry(Long countryId, Long tourId) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new RuntimeException(COUNTRY_NOT_FOUND));
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        country.getTours().remove(tour);
        tour.getCountries().remove(country);
        return getCountryDto(countryId, tourId, country, tour);
    }
}
