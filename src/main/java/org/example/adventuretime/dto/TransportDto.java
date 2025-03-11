package org.example.adventuretime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportDto {
    private Long id;
    private String name;
    private Integer capacity;
    private Double cost;
}
