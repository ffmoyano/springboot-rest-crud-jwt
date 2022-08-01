package com.ffmoyano.idunn.service;


import com.ffmoyano.idunn.dto.AdventurerDto;
import com.ffmoyano.idunn.entity.Adventurer;
import com.ffmoyano.idunn.repository.AdventurerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdventurerService {

    private final AdventurerRepository repository;
    private final ModelMapper modelMapper;

    public AdventurerService(AdventurerRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<AdventurerDto> findAll() {
        List<Adventurer> adventurers = repository.findAll();
        return adventurers.stream()
                .map(adventurer -> modelMapper.map(adventurer, AdventurerDto.class)).toList();
    }

    @Transactional(readOnly = true)
    public AdventurerDto findById(long id) {
        Adventurer adventurer = repository.findById(id).orElse(null);
        return adventurer != null ? modelMapper.map(adventurer, AdventurerDto.class) : null;
    }

    @Transactional(readOnly = false)
    public AdventurerDto save(AdventurerDto adventurerDto) {
        Adventurer adventurer = modelMapper.map(adventurerDto, Adventurer.class);
        adventurer = repository.save(adventurer);
        return modelMapper.map(adventurer, AdventurerDto.class);
    }

    @Transactional(readOnly = false)
    public void delete(AdventurerDto adventurerDto) {
        Adventurer adventurer = modelMapper.map(adventurerDto, Adventurer.class);
        repository.delete(adventurer);
    }
}
