package org.example.adventuretime.config;

import java.util.Collection;
import org.example.adventuretime.dto.CountryDto;
import org.example.adventuretime.dto.TourDto;
import org.example.adventuretime.dto.TransportDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CacheConfig {
    private static final int MAX_TOUR_CACHE_SIZE = 100;
    private static final int MAX_COUNTRY_CACHE_SIZE = 100;
    private static final int MAX_TRANSPORT_CACHE_SIZE = 100;
    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    private final LruCache<Long, TourDto> tourCache = new LruCache<>(MAX_TOUR_CACHE_SIZE);
    private final LruCache<Long, CountryDto> countryCache = new LruCache<>(MAX_COUNTRY_CACHE_SIZE);
    private final LruCache<Long, TransportDto> transportCache =
            new LruCache<>(MAX_TRANSPORT_CACHE_SIZE);

    public TourDto getTour(Long id) {
        logger.debug("Запрос к кэшу туров для ID: {}", id);
        return tourCache.get(id);
    }

    public void putTour(Long id, TourDto tourDto) {
        tourCache.put(id, tourDto);
    }

    public void removeTour(Long id) {
        tourCache.remove(id);
    }

    public Collection<TourDto> getAllTours() {
        logger.debug("Запрос всех туров из кэша");
        return tourCache.getAll();
    }

    public CountryDto getCountry(Long id) {
        logger.debug("Запрос к кэшу стран для ID: {}", id);
        return countryCache.get(id);
    }

    public void putCountry(Long id, CountryDto countryDto) {
        countryCache.put(id, countryDto);
    }

    public void removeCountry(Long id) {
        countryCache.remove(id);
    }

    public TransportDto getTransport(Long id) {
        logger.debug("Запрос к кэшу транспорта для ID: {}", id);
        return transportCache.get(id);
    }

    public void putTransport(Long id, TransportDto transportDto) {
        transportCache.put(id, transportDto);
    }

    public void removeTransport(Long id) {
        transportCache.remove(id);
    }

    public Collection<TransportDto> getAllTransports() {
        logger.debug("Запрос всех транспортов из кэша");
        return transportCache.getAll();
    }

    public Collection<CountryDto> getAllCountries() {
        logger.debug("Запрос всех стран из кэша");
        return countryCache.getAll();
    }
}