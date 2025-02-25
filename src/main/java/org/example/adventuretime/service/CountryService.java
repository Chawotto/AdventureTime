package org.example.adventuretime.service;

import java.util.List;
import java.util.Optional;
import org.example.adventuretime.model.Country;
import org.example.adventuretime.repository.CountryRepository;
import org.springframework.stereotype.Service;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<Country> findAll() {
        return countryRepository.findAll();
    }

    public Optional<Country> findById(Long id) {
        return countryRepository.findById(id);
    }

    public Optional<Country> findByName(String name) {
        return Optional.ofNullable(countryRepository.findByName(name));
    }

    public List<Country> findByNameLike(String namePattern) {
        return countryRepository.findByNameLike(namePattern);
    }

    public Country save(Country country) {
        return countryRepository.save(country);
    }

    public void deleteById(Long id) {
        countryRepository.deleteById(id);
    }
}