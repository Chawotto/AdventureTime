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
import org.example.adventuretime.dto.TransportDto;
import org.example.adventuretime.mapper.CountryMapper;
import org.example.adventuretime.mapper.TourMapper;
import org.example.adventuretime.mapper.TransportMapper;
import org.example.adventuretime.model.Country;
import org.example.adventuretime.model.Tour;
import org.example.adventuretime.model.Transport;
import org.example.adventuretime.repository.CountryRepository;
import org.example.adventuretime.repository.TourRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TourService {

    private static final String TOUR_NOT_FOUND = "Tour not found";

    private final TourRepository tourRepository;
    private final CountryRepository countryRepository;
    private final CacheConfig cacheConfig;

    public List<TourDto> findAll() {
        Collection<TourDto> cachedTours = cacheConfig.getAllTours();
        if (!cachedTours.isEmpty()) {
            return new ArrayList<>(cachedTours);
        }
        List<TourDto> tours = tourRepository.findAll().stream()
                .map(TourMapper::toDto)
                .toList();
        tours.forEach(tour -> cacheConfig.putTour(tour.getId(), tour));
        return tours;
    }

    public Optional<TourDto> findById(Long id) {
        TourDto cachedTour = cacheConfig.getTour(id);
        if (cachedTour != null) {
            return Optional.of(cachedTour);
        }
        Optional<TourDto> tourDto = tourRepository.findById(id)
                .map(TourMapper::toDto);
        tourDto.ifPresent(tour -> cacheConfig.putTour(id, tour));
        return tourDto;
    }

    public TourDto save(TourDto tourDto) {
        Tour tour = TourMapper.toEntity(tourDto);
        Tour saved = tourRepository.save(tour);
        TourDto savedDto = TourMapper.toDto(saved);
        cacheConfig.putTour(savedDto.getId(), savedDto);
        return savedDto;
    }

    @Transactional
    public void deleteById(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(TOUR_NOT_FOUND));
        List<Country> countriesToUpdate = new ArrayList<>(tour.getCountries());

        for (Country country : countriesToUpdate) {
            country.getTours().remove(tour);
            countryRepository.save(country);
            CountryDto countryDto = CountryMapper.toDto(country);
            cacheConfig.putCountry(country.getId(), countryDto);
        }

        tourRepository.delete(tour);
        cacheConfig.removeTour(id);
    }

    @Transactional
    public TourDto updateTour(Long id, TourDto tourDto) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(TOUR_NOT_FOUND));
        tour.setName(tourDto.getName());
        tour.setDescription(tourDto.getDescription());
        tour.setDurationDays(tourDto.getDurationDays());
        Tour updatedTour = tourRepository.save(tour);
        TourDto updatedDto = TourMapper.toDto(updatedTour);

        cacheConfig.putTour(id, updatedDto);

        for (Country country : tour.getCountries()) {
            CountryDto countryDto = CountryMapper.toDto(country);
            cacheConfig.putCountry(country.getId(), countryDto);
        }

        if (tour.getTransport() != null) {
            TransportDto transportDto = TransportMapper.toDto(tour.getTransport());
            cacheConfig.putTransport(tour.getTransport().getId(), transportDto);
        }

        return updatedDto;
    }

    @Transactional
    public TourDto addOrUpdateTransportInTour(Long tourId, TransportDto transportDto) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException(TOUR_NOT_FOUND));
        Transport transport = TransportMapper.toEntity(transportDto);
        tour.setTransport(transport);
        tourRepository.save(tour);

        cacheConfig.putTransport(transport.getId(), transportDto);

        List<Country> countries = new ArrayList<>(tour.getCountries());
        for (Country country : countries) {
            CountryDto countryDto = CountryMapper.toDto(country);
            cacheConfig.putCountry(country.getId(), countryDto);
        }

        TourDto tourDto = TourMapper.toDto(tour);
        cacheConfig.putTour(tourId, tourDto);
        return tourDto;
    }

    @Transactional
    public TourDto removeTransportFromTour(Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException(TOUR_NOT_FOUND));
        tour.setTransport(null);
        Tour updatedTour = tourRepository.save(tour);
        TourDto updatedDto = TourMapper.toDto(updatedTour);

        cacheConfig.putTour(tourId, updatedDto);
        return updatedDto;
    }

    @Transactional
    public List<TourDto> findToursByTransportType(String name) {
        return tourRepository.findToursByTransportTypeJpql(name).stream()
                .map(TourMapper::toDto)
                .toList();
    }

    @Transactional
    public List<TourDto> findToursByTransportTypeNative(String name) {
        return tourRepository.findToursByTransportTypeNative(name).stream()
                .map(TourMapper::toDto)
                .toList();
    }
}

