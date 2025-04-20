package org.example.adventuretime.service;

import org.example.adventuretime.config.CacheConfig;
import org.example.adventuretime.dto.CountryDto;
import org.example.adventuretime.mapper.TourMapper;
import org.example.adventuretime.model.Country;
import org.example.adventuretime.model.Tour;
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

 class CountryServiceTest {

   @Mock
   private CountryRepository countryRepository;

   @Mock
   private TourRepository tourRepository;

   @Mock
   private CacheConfig cacheConfig;

   @InjectMocks
   private CountryService countryService;

   @BeforeEach
   void setUp() {
      MockitoAnnotations.openMocks(this);
   }

   @Test
   void testFindAll_FromCache() {
      CountryDto cachedCountry = new CountryDto();
      cachedCountry.setId(1L);
      cachedCountry.setName("Italy");
      when(cacheConfig.getAllCountries()).thenReturn(Collections.singletonList(cachedCountry));

      List<CountryDto> result = countryService.findAll();

      assertEquals(1, result.size());
      assertEquals("Italy", result.get(0).getName());
      verify(countryRepository, never()).findAll();
   }

   @Test
   void testFindAll_FromRepository() {
      Country country = new Country();
      country.setId(1L);
      country.setName("Spain");
      when(cacheConfig.getAllCountries()).thenReturn(Collections.emptyList());
      when(countryRepository.findAll()).thenReturn(Collections.singletonList(country));

      List<CountryDto> result = countryService.findAll();

      assertEquals(1, result.size());
      assertEquals("Spain", result.get(0).getName());
      verify(cacheConfig).putCountry(1L, result.get(0));
   }

   @Test
   void testFindById_FromCache() {
      CountryDto cachedCountry = new CountryDto();
      cachedCountry.setId(1L);
      cachedCountry.setName("Italy");
      when(cacheConfig.getCountry(1L)).thenReturn(cachedCountry);

      Optional<CountryDto> result = countryService.findById(1L);

      assertTrue(result.isPresent());
      assertEquals("Italy", result.get().getName());
      verify(countryRepository, never()).findById(anyLong());
   }

   @Test
   void testFindById_FromRepository() {
      Country country = new Country();
      country.setId(1L);
      country.setName("Spain");
      when(cacheConfig.getCountry(1L)).thenReturn(null);
      when(countryRepository.findById(1L)).thenReturn(Optional.of(country));

      Optional<CountryDto> result = countryService.findById(1L);

      assertTrue(result.isPresent());
      assertEquals("Spain", result.get().getName());
      verify(cacheConfig).putCountry(1L, result.get());
   }

   @Test
   void testFindById_NotFound() {
      when(cacheConfig.getCountry(1L)).thenReturn(null);
      when(countryRepository.findById(1L)).thenReturn(Optional.empty());

      Optional<CountryDto> result = countryService.findById(1L);

      assertFalse(result.isPresent());
   }

   @Test
   void testSaveCountry_Success() {
      CountryDto countryDto = new CountryDto();
      countryDto.setName("France");
      countryDto.setAttractions("Eiffel Tower");
      countryDto.setVisaCost(80.0);
      countryDto.setNationalLanguages("French");

      Country savedCountry = new Country();
      savedCountry.setId(1L);
      savedCountry.setName("France");
      savedCountry.setAttractions("Eiffel Tower");
      savedCountry.setVisaCost(80.0);
      savedCountry.setNationalLanguages("French");

      when(countryRepository.save(any(Country.class))).thenReturn(savedCountry);

      CountryDto result = countryService.save(countryDto);

      assertEquals(1L, result.getId());
      assertEquals("France", result.getName());
      verify(cacheConfig).putCountry(1L, result);
   }

   @Test
   void testUpdateCountry_Success() {
      Country existingCountry = new Country();
      existingCountry.setId(1L);
      existingCountry.setName("Old Name");

      CountryDto countryDto = new CountryDto();
      countryDto.setName("New Name");
      countryDto.setAttractions("New Attractions");
      countryDto.setVisaCost(100.0);
      countryDto.setNationalLanguages("New Language");

      when(countryRepository.findById(1L)).thenReturn(Optional.of(existingCountry));
      when(countryRepository.save(any(Country.class))).thenAnswer(invocation -> invocation.getArgument(0));

      CountryDto result = countryService.updateCountry(1L, countryDto);

      assertEquals("New Name", result.getName());
      verify(cacheConfig).putCountry(1L, result);
   }

   @Test
   void testUpdateCountry_NotFound() {
      CountryDto countryDto = new CountryDto();
      countryDto.setName("New Name");

      when(countryRepository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(RuntimeException.class, () -> countryService.updateCountry(1L, countryDto));
   }

   @Test
   void testDeleteCountry_Success() {
      Country country = new Country();
      country.setId(1L);
      when(countryRepository.findById(1L)).thenReturn(Optional.of(country));

      countryService.deleteCountry(1L);

      verify(countryRepository).delete(country);
      verify(cacheConfig).removeCountry(1L);
   }

   @Test
   void testDeleteCountry_NotFound() {
      when(countryRepository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(RuntimeException.class, () -> countryService.deleteCountry(1L));
   }

   @Test
   void testAddTourToCountry_Success() {
      Country country = new Country();
      country.setId(1L);
      Tour tour = new Tour();
      tour.setId(1L);

      when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
      when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
      when(countryRepository.save(any(Country.class))).thenReturn(country);
      when(tourRepository.save(any(Tour.class))).thenReturn(tour);

      CountryDto result = countryService.addTourToCountry(1L, 1L);

      assertNotNull(result);
      verify(cacheConfig).putCountry(1L, result);
      verify(cacheConfig).putTour(1L, TourMapper.toDto(tour));
   }

   @Test
   void testAddTourToCountry_CountryNotFound() {
      when(countryRepository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(RuntimeException.class, () -> countryService.addTourToCountry(1L, 1L));
   }

   @Test
   void testAddTourToCountry_TourNotFound() {
      Country country = new Country();
      country.setId(1L);

      when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
      when(tourRepository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(RuntimeException.class, () -> countryService.addTourToCountry(1L, 1L));
   }

   @Test
   void testRemoveTourFromCountry_Success() {
      Country country = new Country();
      country.setId(1L);
      Tour tour = new Tour();
      tour.setId(1L);
      country.getTours().add(tour);
      tour.getCountries().add(country);

      when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
      when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
      when(countryRepository.save(any(Country.class))).thenReturn(country);
      when(tourRepository.save(any(Tour.class))).thenReturn(tour);

      CountryDto result = countryService.removeTourFromCountry(1L, 1L);

      assertNotNull(result);
      verify(cacheConfig).putCountry(1L, result);
      verify(cacheConfig).putTour(1L, TourMapper.toDto(tour));
   }

   @Test
   void testRemoveTourFromCountry_CountryNotFound() {
      when(countryRepository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(RuntimeException.class, () -> countryService.removeTourFromCountry(1L, 1L));
   }

   @Test
   void testRemoveTourFromCountry_TourNotFound() {
      Country country = new Country();
      country.setId(1L);

      when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
      when(tourRepository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(RuntimeException.class, () -> countryService.removeTourFromCountry(1L, 1L));
   }
}