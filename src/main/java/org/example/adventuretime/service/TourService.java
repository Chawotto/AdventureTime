package org.example.adventuretime.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.adventuretime.dto.TourDto;
import org.example.adventuretime.mapper.TourMapper;
import org.example.adventuretime.model.Tour;
import org.example.adventuretime.model.Transport;
import org.example.adventuretime.repository.TourRepository;
import org.example.adventuretime.repository.TransportRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TourService {

    private static final String TOUR_NOT_FOUND = "Tour not found";

    private final TourRepository tourRepository;
    private final TransportRepository transportRepository;

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
                .orElseThrow(() -> new RuntimeException(TOUR_NOT_FOUND));
        tour.setName(tourDto.getName());
        Tour updated = tourRepository.save(tour);
        return TourMapper.toDto(updated);
    }

    @Transactional
    public TourDto addOrUpdateTransportInTour(Long tourId, Long transportId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException(TOUR_NOT_FOUND));
        Transport transport = transportRepository.findById(transportId)
                .orElseThrow(() -> new RuntimeException(TOUR_NOT_FOUND));
        tour.setTransport(transport);
        Tour updatedTour = tourRepository.save(tour);
        return TourMapper.toDto(updatedTour);
    }

    @Transactional
    public TourDto removeTransportFromTour(Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException(TOUR_NOT_FOUND));
        tour.setTransport(null);
        Tour updatedTour = tourRepository.save(tour);
        return TourMapper.toDto(updatedTour);
    }

    @Transactional
    public List<TourDto> findToursByTransportType(String name) {
        return tourRepository.findToursByTransportTypeJpql(name).stream()
                .map(TourMapper::toDto)
                .toList();
    }
}
