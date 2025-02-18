package org.example.adventuretime.dao;

import java.util.List;
import java.util.Optional;
import org.example.adventuretime.Tour;
import org.example.adventuretime.TourRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TourDaoImpl implements TourDao {

    private final TourRepository tourRepository;

    public TourDaoImpl(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    @Override
    public List<Tour> findAll() {
        return tourRepository.findAll();
    }

    @Override
    public Optional<Tour> findById(Long id) {
        return tourRepository.findById(id);
    }

    @Override
    public Tour save(Tour tour) {
        return tourRepository.save(tour);
    }

    @Override
    public void deleteById(Long id) {
        tourRepository.deleteById(id);
    }
}