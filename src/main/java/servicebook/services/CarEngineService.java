package servicebook.services;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import servicebook.entity.engine.CarEngine;

import servicebook.repository.CarEngineRepository;

@Service
@RequiredArgsConstructor
public class CarEngineService {

    private final CarEngineRepository carEngineRepository;

    @Transactional
    public void delete(CarEngine engine) {
        carEngineRepository.delete(engine);
    }

    @Transactional(readOnly = true)
    public CarEngine getEngineById(Long id) {
        return carEngineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void saveOrUpdate(CarEngine engine) {
        carEngineRepository.save(engine);
    }
}
