package org.example.adventuretime.dao;

import java.util.List;
import java.util.Optional;
import org.example.adventuretime.Country;
import org.example.adventuretime.CountryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CountryDaoImpl implements CountryDao {

    private final CountryRepository countryRepository;

    public CountryDaoImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public List<Country> findAll() {
        return countryRepository.findAll();
    }

    @Override
    public Optional<Country> findById(Long id) {
        return countryRepository.findById(id);
    }

    @Override
    public Optional<Country> findByName(String name) {
        return Optional.ofNullable(countryRepository.findByName(name));
    }

    @Override
    public Country save(Country country) {
        return countryRepository.save(country);
    }

    @Override
    public void deleteById(Long id) {
        countryRepository.deleteById(id);
    }
}