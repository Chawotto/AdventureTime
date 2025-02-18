package org.example.adventuretime.dao;

import java.util.List;
import java.util.Optional;
import org.example.adventuretime.Country;

public interface CountryDao {
    List<Country> findAll();
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    Optional<Country> findById(Long id);
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    Optional<Country> findByName(String name);
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    Country save(Country country);
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    void deleteById(Long id);
}