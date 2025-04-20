package org.example.adventuretime.mapper;

import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.example.adventuretime.dto.CountryDto;
import org.example.adventuretime.model.Country;

@UtilityClass
public class CountryMapper {

    public CountryDto toDto(Country country) {
        if (country == null) return null;
        CountryDto dto = new CountryDto();
        dto.setId(country.getId());
        dto.setName(country.getName());
        dto.setAvailable(country.isAvailable());
        dto.setAttractions(country.getAttractions());
        dto.setVisaCost(country.getVisaCost());
        dto.setNationalLanguages(country.getNationalLanguages());
        if (country.getTours() != null) {
            dto.setTours(country.getTours().stream()
                    .map(TourMapper::toDtoShallow)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }

    public CountryDto toDtoShallow(Country country) {
        if (country == null) return null;
        CountryDto dto = new CountryDto();
        dto.setId(country.getId());
        dto.setName(country.getName());
        dto.setAttractions(country.getAttractions());
        dto.setVisaCost(country.getVisaCost());
        dto.setNationalLanguages(country.getNationalLanguages());
        dto.setAvailable(country.isAvailable());
        return dto;
    }

    public Country toEntity(CountryDto dto) {
        if (dto == null) return null;
        Country country = new Country();
        country.setId(dto.getId());
        country.setName(dto.getName());
        country.setAvailable(dto.isAvailable());
        country.setAttractions(dto.getAttractions());
        country.setVisaCost(dto.getVisaCost());
        country.setNationalLanguages(dto.getNationalLanguages());
        return country;
    }
}
