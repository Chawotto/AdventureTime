package org.example.adventuretime.mapper;

import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.example.adventuretime.dto.TourDto;
import org.example.adventuretime.model.Tour;

@UtilityClass
public class TourMapper {

    public TourDto toDto(Tour tour) {
        if (tour == null) return null;
        TourDto dto = new TourDto();
        dto.setId(tour.getId());
        dto.setName(tour.getName());
        dto.setDescription(tour.getDescription());
        dto.setDurationDays(tour.getDurationDays());
        if (tour.getCountries() != null) {
            dto.setCountries(tour.getCountries().stream()
                    .map(CountryMapper::toDtoShallow)
                    .collect(Collectors.toSet()));
        }
        if (tour.getTransport() != null) {
            dto.setTransport(TransportMapper.toDto(tour.getTransport()));
        }
        return dto;
    }

    public TourDto toDtoShallow(Tour tour) {
        if (tour == null) return null;
        TourDto dto = new TourDto();
        dto.setId(tour.getId());
        dto.setName(tour.getName());
        dto.setDescription(tour.getDescription());
        dto.setDurationDays(tour.getDurationDays());
        dto.setTransport(TransportMapper.toDto(tour.getTransport()));
        return dto;
    }

    public Tour toEntity(TourDto dto) {
        if (dto == null) return null;
        Tour tour = new Tour();
        tour.setId(dto.getId());
        tour.setName(dto.getName());
        tour.setDescription(dto.getDescription());
        tour.setDurationDays(dto.getDurationDays());
        if (dto.getTransport() != null) {
            tour.setTransport(TransportMapper.toEntity(dto.getTransport()));
        }
        return tour;
    }
}
