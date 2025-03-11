package org.example.adventuretime.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.adventuretime.dto.CountryDto;
import org.example.adventuretime.mapper.CountryMapper;
import org.example.adventuretime.model.Country;
import org.example.adventuretime.model.Tour;
import org.example.adventuretime.repository.CountryRepository;
import org.example.adventuretime.repository.TourRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CountryService {

    private static final String COUNTRY_NOT_FOUND = "Country not found";

    private final CountryRepository countryRepository;
    private final TourRepository tourRepository;

    public List<CountryDto> findAll() {
        return countryRepository.findAll().stream()
                .map(CountryMapper::toDto)
                .toList();
    }

    public Optional<CountryDto> findById(Long id) {
        return countryRepository.findById(id)
                .map(CountryMapper::toDto);
    }

    public CountryDto save(CountryDto countryDto) {
        Country country = CountryMapper.toEntity(countryDto);
        Country saved = countryRepository.save(country);
        return CountryMapper.toDto(saved);
    }

    @Transactional
    public CountryDto createCountryWithTours(CountryDto countryDto, List<Long> tourIds) {
        Country country = CountryMapper.toEntity(countryDto);
        Set<Tour> tours = new HashSet<>(tourRepository.findAllById(tourIds));
        country.setTours(tours);
        tours.forEach(tour -> tour.getCountries().add(country));
        Country saved = countryRepository.save(country);
        return CountryMapper.toDto(saved);
    }

    @Transactional
    public void deleteCountry(Long countryId) {
        Optional<Country> countryOpt = countryRepository.findById(countryId);
        if (countryOpt.isPresent()) {
            Country country = countryOpt.get();

            for (Tour tour : country.getTours()) {
                tour.getCountries().remove(country);
                tourRepository.save(tour);
            }

            countryRepository.delete(country);
        }
    }

    public List<CountryDto> findByNameLike(String namePattern) {
        return countryRepository.findByNameLike(namePattern).stream()
                .map(CountryMapper::toDto)
                .toList();
    }

    @Transactional
    public CountryDto updateCountry(Long id, CountryDto countryDto) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(COUNTRY_NOT_FOUND));
        country.setName(countryDto.getName());
        country.setAvailable(countryDto.isAvailable());
        Country updated = countryRepository.save(country);
        return CountryMapper.toDto(updated);
    }

    @Transactional
    public CountryDto addTourToCountry(Long countryId, Long tourId) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new RuntimeException(COUNTRY_NOT_FOUND));
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        country.getTours().add(tour);
        tour.getCountries().add(country);
        countryRepository.save(country);
        tourRepository.save(tour);
        return CountryMapper.toDto(country);
    }

    @Transactional
    public CountryDto removeTourFromCountry(Long countryId, Long tourId) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new RuntimeException(COUNTRY_NOT_FOUND));
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        country.getTours().remove(tour);
        tour.getCountries().remove(country);
        countryRepository.save(country);
        tourRepository.save(tour);
        return CountryMapper.toDto(country);
    }
}
