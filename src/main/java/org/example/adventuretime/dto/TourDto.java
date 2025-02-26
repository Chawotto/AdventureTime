package org.example.adventuretime.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourDto {
    private Long id;
    private String name;
    private Set<CountryDto> countries;
}
