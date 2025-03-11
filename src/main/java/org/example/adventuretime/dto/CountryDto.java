package org.example.adventuretime.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryDto {
    private Long id;
    private String name;
    private boolean available;
    private String attractions;
    private Double visaCost;
    private String nationalLanguages;
    private Set<TourDto> tours;
}
