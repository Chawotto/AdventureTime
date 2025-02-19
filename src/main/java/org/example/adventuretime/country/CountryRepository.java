package org.example.adventuretime.country;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Country findByName(String name);
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    List<Country> findByNameLike(String namePattern);
}