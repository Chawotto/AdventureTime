package org.example.adventuretime.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.adventuretime.dto.TransportDto;
import org.example.adventuretime.mapper.TransportMapper;
import org.example.adventuretime.model.Transport;
import org.example.adventuretime.repository.TransportRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransportService {
    private final TransportRepository transportRepository;

    public List<TransportDto> findAll() {
        return transportRepository.findAll().stream()
                .map(TransportMapper::toDto)
                .toList();
    }

    public Optional<TransportDto> findById(Long id) {
        return transportRepository.findById(id)
                .map(TransportMapper::toDto);
    }

    public TransportDto save(TransportDto transportDto) {
        Transport transport = TransportMapper.toEntity(transportDto);
        Transport saved = transportRepository.save(transport);
        return TransportMapper.toDto(saved);
    }

    public TransportDto updateTransport(Long id, TransportDto transportDto) {
        Transport transport = transportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transport not found"));
        transport.setName(transportDto.getName());
        Transport updated = transportRepository.save(transport);
        return TransportMapper.toDto(updated);
    }

    public void deleteById(Long id) {
        transportRepository.deleteById(id);
    }
}
