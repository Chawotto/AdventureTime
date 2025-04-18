package org.example.adventuretime.service;

import org.example.adventuretime.config.CacheConfig;
import org.example.adventuretime.dto.TourDto;
import org.example.adventuretime.dto.TransportDto;
import org.example.adventuretime.mapper.CountryMapper;
import org.example.adventuretime.model.Country;
import org.example.adventuretime.model.Tour;
import org.example.adventuretime.model.Transport;
import org.example.adventuretime.repository.CountryRepository;
import org.example.adventuretime.repository.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

 class TourServiceTest {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CacheConfig cacheConfig;

    @InjectMocks
    private TourService tourService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Тесты для метода findAll
    @Test
    void testFindAll_FromCache() {
        TourDto cachedTour = new TourDto();
        cachedTour.setId(1L);
        cachedTour.setName("Grand Tour");
        when(cacheConfig.getAllTours()).thenReturn(Collections.singletonList(cachedTour));

        List<TourDto> result = tourService.findAll();

        assertEquals(1, result.size());
        assertEquals("Grand Tour", result.get(0).getName());
        verify(tourRepository, never()).findAll();
    }

    @Test
    void testFindAll_FromRepository() {
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setName("Europe Tour");
        when(cacheConfig.getAllTours()).thenReturn(Collections.emptyList());
        when(tourRepository.findAll()).thenReturn(Collections.singletonList(tour));

        List<TourDto> result = tourService.findAll();

        assertEquals(1, result.size());
        assertEquals("Europe Tour", result.get(0).getName());
        verify(cacheConfig).putTour(1L, result.get(0));
    }

    // Тесты для метода findById
    @Test
    void testFindById_FromCache() {
        TourDto cachedTour = new TourDto();
        cachedTour.setId(1L);
        cachedTour.setName("Grand Tour");
        when(cacheConfig.getTour(1L)).thenReturn(cachedTour);

        Optional<TourDto> result = tourService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Grand Tour", result.get().getName());
        verify(tourRepository, never()).findById(anyLong());
    }

    @Test
    void testFindById_FromRepository() {
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setName("Europe Tour");
        when(cacheConfig.getTour(1L)).thenReturn(null);
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        Optional<TourDto> result = tourService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Europe Tour", result.get().getName());
        verify(cacheConfig).putTour(1L, result.get());
    }

    @Test
    void testFindById_NotFound() {
        when(cacheConfig.getTour(1L)).thenReturn(null);
        when(tourRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<TourDto> result = tourService.findById(1L);

        assertFalse(result.isPresent());
    }

    // Тесты для метода save
    @Test
    void testSaveTour_Success() {
        TourDto tourDto = new TourDto();
        tourDto.setName("Grand Tour");
        tourDto.setDescription("A tour of Europe");
        tourDto.setDurationDays(10);

        Tour savedTour = new Tour();
        savedTour.setId(1L);
        savedTour.setName("Grand Tour");
        savedTour.setDescription("A tour of Europe");
        savedTour.setDurationDays(10);

        when(tourRepository.save(any(Tour.class))).thenReturn(savedTour);

        TourDto result = tourService.save(tourDto);

        assertEquals(1L, result.getId());
        assertEquals("Grand Tour", result.getName());
        verify(cacheConfig).putTour(1L, result);
    }

    // Тесты для метода updateTour
    @Test
    void testUpdateTour_Success() {
        Tour existingTour = new Tour();
        existingTour.setId(1L);
        existingTour.setName("Old Tour");

        TourDto tourDto = new TourDto();
        tourDto.setName("New Tour");
        tourDto.setDescription("Updated description");
        tourDto.setDurationDays(15);

        when(tourRepository.findById(1L)).thenReturn(Optional.of(existingTour));
        when(tourRepository.save(any(Tour.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TourDto result = tourService.updateTour(1L, tourDto);

        assertEquals("New Tour", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals(15, result.getDurationDays());
        verify(cacheConfig).putTour(1L, result);
    }

    @Test
    void testUpdateTour_NotFound() {
        TourDto tourDto = new TourDto();
        tourDto.setName("New Tour");

        when(tourRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tourService.updateTour(1L, tourDto));
    }

    // Тесты для метода deleteById
    @Test
    void testDeleteById_Success() {
        Tour tour = new Tour();
        tour.setId(1L);
        Country country = new Country();
        country.setId(1L);
        tour.getCountries().add(country);
        country.getTours().add(tour);

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(countryRepository.save(any(Country.class))).thenReturn(country);

        tourService.deleteById(1L);

        verify(tourRepository).delete(tour);
        verify(cacheConfig).removeTour(1L);
        verify(cacheConfig).putCountry(1L, CountryMapper.toDto(country));
    }

    @Test
    void testDeleteById_NotFound() {
        when(tourRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tourService.deleteById(1L));
    }

    // Тесты для метода addOrUpdateTransportInTour
    @Test
    void testAddOrUpdateTransportInTour_Success() {
        Tour tour = new Tour();
        tour.setId(1L);
        TransportDto transportDto = new TransportDto();
        transportDto.setId(1L);
        transportDto.setName("Bus");

        Country country = new Country();
        country.setId(1L);
        tour.getCountries().add(country);

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(tourRepository.save(any(Tour.class))).thenReturn(tour);

        TourDto result = tourService.addOrUpdateTransportInTour(1L, transportDto);

        assertNotNull(result);
        verify(cacheConfig).putTransport(1L, transportDto);
        verify(cacheConfig).putCountry(1L, CountryMapper.toDto(country));
        verify(cacheConfig).putTour(1L, result);
    }

    @Test
    void testAddOrUpdateTransportInTour_TourNotFound() {
        TransportDto transportDto = new TransportDto();
        transportDto.setId(1L);

        when(tourRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tourService.addOrUpdateTransportInTour(1L, transportDto));
    }

    // Тесты для метода removeTransportFromTour
    @Test
    void testRemoveTransportFromTour_Success() {
        Tour tour = new Tour();
        tour.setId(1L);
        Transport transport = new Transport();
        transport.setId(1L);
        tour.setTransport(transport);

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(tourRepository.save(any(Tour.class))).thenReturn(tour);

        TourDto result = tourService.removeTransportFromTour(1L);

        assertNotNull(result);
        assertNull(tour.getTransport());
        verify(cacheConfig).putTour(1L, result);
    }

    @Test
    void testRemoveTransportFromTour_TourNotFound() {
        when(tourRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tourService.removeTransportFromTour(1L));
    }

    // Тесты для метода findToursByTransportType (JPQL)
    @Test
    void testFindToursByTransportType_Success() {
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setName("Grand Tour");
        Transport transport = new Transport();
        transport.setName("Bus");
        tour.setTransport(transport);

        when(tourRepository.findToursByTransportTypeJpql("Bus")).thenReturn(Collections.singletonList(tour));

        List<TourDto> result = tourService.findToursByTransportType("Bus");

        assertEquals(1, result.size());
        assertEquals("Grand Tour", result.get(0).getName());
    }

    @Test
    void testFindToursByTransportType_NoToursFound() {
        when(tourRepository.findToursByTransportTypeJpql("Bus")).thenReturn(Collections.emptyList());

        List<TourDto> result = tourService.findToursByTransportType("Bus");

        assertTrue(result.isEmpty());
    }

    // Тесты для метода findToursByTransportTypeNative (нативный запрос)
    @Test
    void testFindToursByTransportTypeNative_Success() {
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setName("Grand Tour");
        Transport transport = new Transport();
        transport.setName("Bus");
        tour.setTransport(transport);

        when(tourRepository.findToursByTransportTypeNative("Bus")).thenReturn(Collections.singletonList(tour));

        List<TourDto> result = tourService.findToursByTransportTypeNative("Bus");

        assertEquals(1, result.size());
        assertEquals("Grand Tour", result.get(0).getName());
    }

    @Test
    void testFindToursByTransportTypeNative_NoToursFound() {
        when(tourRepository.findToursByTransportTypeNative("Bus")).thenReturn(Collections.emptyList());

        List<TourDto> result = tourService.findToursByTransportTypeNative("Bus");

        assertTrue(result.isEmpty());
    }
}