package org.example.adventuretime.service;

import java.util.List;
import java.util.Optional;
import org.example.adventuretime.model.Tour;
import org.example.adventuretime.repository.TourRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TourService {

    private final TourRepository tourRepository;

    public TourService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public List<Tour> findAll() {
        return tourRepository.findAll();
    }

    public Optional<Tour> findById(Long id) {
        return tourRepository.findById(id);
    }

    public Tour save(Tour tour) {
        return tourRepository.save(tour);
    }

    public void deleteById(Long id) {
        tourRepository.deleteById(id);
    }
}