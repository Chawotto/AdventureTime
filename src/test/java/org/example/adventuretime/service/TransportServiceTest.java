package org.example.adventuretime.service;

import org.example.adventuretime.config.CacheConfig;
import org.example.adventuretime.dto.CountryDto;
import org.example.adventuretime.dto.TourDto;
import org.example.adventuretime.dto.TransportDto;
import org.example.adventuretime.mapper.CountryMapper;
import org.example.adventuretime.mapper.TourMapper;
import org.example.adventuretime.model.Country;
import org.example.adventuretime.model.Tour;
import org.example.adventuretime.model.Transport;
import org.example.adventuretime.repository.TourRepository;
import org.example.adventuretime.repository.TransportRepository;
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

 class TransportServiceTest {

    @Mock
    private TransportRepository transportRepository;

    @Mock
    private TourRepository tourRepository;

    @Mock
    private CacheConfig cacheConfig;

    @InjectMocks
    private TransportService transportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Тесты для метода findAll
    @Test
    void testFindAll_FromCache() {
        TransportDto cachedTransport = new TransportDto();
        cachedTransport.setId(1L);
        cachedTransport.setName("Bus");
        when(cacheConfig.getAllTransports()).thenReturn(Collections.singletonList(cachedTransport));

        List<TransportDto> result = transportService.findAll();

        assertEquals(1, result.size());
        assertEquals("Bus", result.get(0).getName());
        verify(transportRepository, never()).findAll();
    }

    @Test
    void testFindAll_FromRepository() {
        Transport transport = new Transport();
        transport.setId(1L);
        transport.setName("Train");
        when(cacheConfig.getAllTransports()).thenReturn(Collections.emptyList());
        when(transportRepository.findAll()).thenReturn(Collections.singletonList(transport));

        List<TransportDto> result = transportService.findAll();

        assertEquals(1, result.size());
        assertEquals("Train", result.get(0).getName());
        verify(cacheConfig).putTransport(1L, result.get(0));
    }

    // Тесты для метода findById
    @Test
    void testFindById_FromCache() {
        TransportDto cachedTransport = new TransportDto();
        cachedTransport.setId(1L);
        cachedTransport.setName("Bus");
        when(cacheConfig.getTransport(1L)).thenReturn(cachedTransport);

        Optional<TransportDto> result = transportService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Bus", result.get().getName());
        verify(transportRepository, never()).findById(anyLong());
    }

    @Test
    void testFindById_FromRepository() {
        Transport transport = new Transport();
        transport.setId(1L);
        transport.setName("Train");
        when(cacheConfig.getTransport(1L)).thenReturn(null);
        when(transportRepository.findById(1L)).thenReturn(Optional.of(transport));

        Optional<TransportDto> result = transportService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Train", result.get().getName());
        verify(cacheConfig).putTransport(1L, result.get());
    }

    @Test
    void testFindById_NotFound() {
        when(cacheConfig.getTransport(1L)).thenReturn(null);
        when(transportRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<TransportDto> result = transportService.findById(1L);

        assertFalse(result.isPresent());
    }

    // Тесты для метода save
    @Test
    void testSaveTransport_Success() {
        TransportDto transportDto = new TransportDto();
        transportDto.setName("Bus");
        transportDto.setCapacity(50);
        transportDto.setCost(100.0);

        Transport savedTransport = new Transport();
        savedTransport.setId(1L);
        savedTransport.setName("Bus");
        savedTransport.setCapacity(50);
        savedTransport.setCost(100.0);

        when(transportRepository.save(any(Transport.class))).thenReturn(savedTransport);

        TransportDto result = transportService.save(transportDto);

        assertEquals(1L, result.getId());
        assertEquals("Bus", result.getName());
        assertEquals(50, result.getCapacity());
        assertEquals(100.0, result.getCost(), 0.01);
        verify(cacheConfig).putTransport(1L, result);
    }

    // Тесты для метода updateTransport
    @Test
    void testUpdateTransport_Success() {
        Transport existingTransport = new Transport();
        existingTransport.setId(1L);
        existingTransport.setName("Old Bus");
        existingTransport.setCapacity(30);
        existingTransport.setCost(50.0);

        Tour tour = new Tour();
        tour.setId(1L);
        tour.setTransport(existingTransport);
        Country country = new Country();
        country.setId(1L);
        tour.getCountries().add(country);

        TransportDto transportDto = new TransportDto();
        transportDto.setName("New Bus");
        transportDto.setCapacity(40);
        transportDto.setCost(75.0);

        when(transportRepository.findById(1L)).thenReturn(Optional.of(existingTransport));
        when(transportRepository.save(any(Transport.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tourRepository.findByTransport(existingTransport)).thenReturn(Collections.singletonList(tour));

        TransportDto result = transportService.updateTransport(1L, transportDto);

        assertEquals("New Bus", result.getName());
        assertEquals(40, result.getCapacity());
        assertEquals(75.0, result.getCost(), 0.01);
        verify(cacheConfig).putTransport(1L, result);
        verify(cacheConfig).putTour(1L, TourMapper.toDto(tour));
        verify(cacheConfig).putCountry(1L, CountryMapper.toDto(country));
    }

    @Test
    void testUpdateTransport_NotFound() {
        TransportDto transportDto = new TransportDto();
        transportDto.setName("New Bus");

        when(transportRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transportService.updateTransport(1L, transportDto));
    }

    // Тесты для метода deleteById
    @Test
    void testDeleteById_Success() {
        Transport transport = new Transport();
        transport.setId(1L);

        Tour tour = new Tour();
        tour.setId(1L);
        tour.setTransport(transport);
        Country country = new Country();
        country.setId(1L);
        tour.getCountries().add(country);

        when(transportRepository.findById(1L)).thenReturn(Optional.of(transport));
        when(tourRepository.findByTransport(transport)).thenReturn(Collections.singletonList(tour));
        when(tourRepository.save(any(Tour.class))).thenReturn(tour);

        transportService.deleteById(1L);

        assertNull(tour.getTransport());
        verify(transportRepository).delete(transport);
        verify(cacheConfig).removeTransport(1L);
        verify(cacheConfig).putTour(1L, TourMapper.toDto(tour));
        verify(cacheConfig).putCountry(1L, CountryMapper.toDto(country));
    }

    @Test
    void testDeleteById_NotFound() {
        when(transportRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transportService.deleteById(1L));
    }

    @Test
    void testDeleteById_NoAssociatedTours() {
        Transport transport = new Transport();
        transport.setId(1L);

        when(transportRepository.findById(1L)).thenReturn(Optional.of(transport));
        when(tourRepository.findByTransport(transport)).thenReturn(Collections.emptyList());

        transportService.deleteById(1L);

        verify(transportRepository).delete(transport);
        verify(cacheConfig).removeTransport(1L);
        verify(tourRepository, never()).save(any(Tour.class));
        verify(cacheConfig, never()).putTour(anyLong(), any(TourDto.class));
        verify(cacheConfig, never()).putCountry(anyLong(), any(CountryDto.class));
    }
}