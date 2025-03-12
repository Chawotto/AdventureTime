package org.example.adventuretime.service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import org.example.adventuretime.repository.TourRepository;
import org.example.adventuretime.repository.TransportRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransportService {

    private static final String TRANSPORT_NOT_FOUND = "Transport not found";

    private final TransportRepository transportRepository;
    private final TourRepository tourRepository;
    private final CacheConfig cacheConfig;

    public List<TransportDto> findAll() {
        Collection<TransportDto> cachedTransports = cacheConfig.getAllTransports();
        if (!cachedTransports.isEmpty()) {
            return new ArrayList<>(cachedTransports);
        }
        List<TransportDto> transports = transportRepository.findAll().stream()
                .map(TransportMapper::toDto)
                .toList();
        transports.forEach(transport -> cacheConfig.putTransport(transport.getId(), transport));
        return transports;
    }

    public Optional<TransportDto> findById(Long id) {
        TransportDto cachedTransport = cacheConfig.getTransport(id);
        if (cachedTransport != null) {
            return Optional.of(cachedTransport);
        }
        Optional<TransportDto> transportDto = transportRepository.findById(id)
                .map(TransportMapper::toDto);
        transportDto.ifPresent(transport -> cacheConfig.putTransport(id, transport));
        return transportDto;
    }

    public TransportDto save(TransportDto transportDto) {
        Transport transport = TransportMapper.toEntity(transportDto);
        Transport saved = transportRepository.save(transport);
        TransportDto savedDto = TransportMapper.toDto(saved);
        cacheConfig.putTransport(savedDto.getId(), savedDto);
        return savedDto;
    }

    @Transactional
    public TransportDto updateTransport(Long id, TransportDto transportDto) {
        Transport transport = transportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(TRANSPORT_NOT_FOUND));
        transport.setName(transportDto.getName());
        transport.setCapacity(transportDto.getCapacity());
        transport.setCost(transportDto.getCost());
        Transport updatedTransport = transportRepository.save(transport);
        TransportDto updatedDto = TransportMapper.toDto(updatedTransport);

        cacheConfig.putTransport(id, updatedDto);

        List<Tour> tours = tourRepository.findByTransport(updatedTransport);
        for (Tour tour : tours) {
            TourDto tourDto = TourMapper.toDto(tour);
            cacheConfig.putTour(tour.getId(), tourDto);
        }

        Set<Country> countriesToUpdate = new HashSet<>();
        for (Tour tour : tours) {
            countriesToUpdate.addAll(tour.getCountries());
        }
        for (Country country : countriesToUpdate) {
            CountryDto countryDto = CountryMapper.toDto(country);
            cacheConfig.putCountry(country.getId(), countryDto);
        }

        return updatedDto;
    }

    @Transactional
    public void deleteById(Long id) {
        Transport transport = transportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(TRANSPORT_NOT_FOUND));
        List<Tour> tours = tourRepository.findByTransport(transport);

        Set<Country> countriesToUpdate = new HashSet<>();
        for (Tour tour : tours) {
            tour.setTransport(null);
            tourRepository.save(tour);
            TourDto tourDto = TourMapper.toDto(tour);
            cacheConfig.putTour(tour.getId(), tourDto);
            countriesToUpdate.addAll(tour.getCountries());
        }

        for (Country country : countriesToUpdate) {
            CountryDto countryDto = CountryMapper.toDto(country);
            cacheConfig.putCountry(country.getId(), countryDto);
        }

        transportRepository.delete(transport);
        cacheConfig.removeTransport(id);
    }
}
