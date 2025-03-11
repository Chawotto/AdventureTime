package org.example.adventuretime.mapper;

import lombok.experimental.UtilityClass;
import org.example.adventuretime.dto.TransportDto;
import org.example.adventuretime.model.Transport;

@UtilityClass
public class TransportMapper {

    public TransportDto toDto(Transport transport) {
        if (transport == null) return null;
        TransportDto dto = new TransportDto();
        dto.setId(transport.getId());
        dto.setName(transport.getName());
        dto.setCapacity(transport.getCapacity());
        dto.setCost(transport.getCost());
        return dto;
    }

    public Transport toEntity(TransportDto dto) {
        if (dto == null) return null;
        Transport transport = new Transport();
        transport.setId(dto.getId());
        transport.setName(dto.getName());
        transport.setCapacity(dto.getCapacity());
        transport.setCost(dto.getCost());
        return transport;
    }
}
