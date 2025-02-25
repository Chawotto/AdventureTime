package org.example.adventuretime.repository;

import java.util.List;
import org.example.adventuretime.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Country findByName(String name);
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    List<Country> findByNameLike(String namePattern);
}
