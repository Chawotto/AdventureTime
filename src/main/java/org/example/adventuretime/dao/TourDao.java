package org.example.adventuretime.dao;

import java.util.List;
import java.util.Optional;
import org.example.adventuretime.Tour;

public interface TourDao {
    List<Tour> findAll();
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    Optional<Tour> findById(Long id);
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    Tour save(Tour tour);
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    void deleteById(Long id);
}