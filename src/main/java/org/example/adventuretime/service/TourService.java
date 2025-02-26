package org.example.adventuretime.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.adventuretime.dto.TourDto;
import org.example.adventuretime.mapper.TourMapper;
import org.example.adventuretime.model.Tour;
import org.example.adventuretime.repository.TourRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TourService {

    private final TourRepository tourRepository;

    public List<TourDto> findAll() {
        return tourRepository.findAll().stream()
                .map(TourMapper::toDto)
                .toList();
    }

    public Optional<TourDto> findById(Long id) {
        return tourRepository.findById(id)
                .map(TourMapper::toDto);
    }

    public TourDto save(TourDto tourDto) {
        Tour tour = TourMapper.toEntity(tourDto);
        Tour saved = tourRepository.save(tour);
        return TourMapper.toDto(saved);
    }

    public void deleteById(Long id) {
        tourRepository.deleteById(id);
    }

    @Transactional
    public TourDto updateTour(Long id, TourDto tourDto) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        tour.setName(tourDto.getName());
        Tour updated = tourRepository.save(tour);
        return TourMapper.toDto(updated);
    }
}
