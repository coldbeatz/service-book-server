package servicebook.services;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import servicebook.entity.engine.CarEngine;

import servicebook.repository.CarEngineRepository;

@Service
@RequiredArgsConstructor
public class CarEngineService {

    private final CarEngineRepository carEngineRepository;

    public CarEngine getEngineById(Long id) {
        return carEngineRepository.findById(id).orElse(null);
    }

    public void saveEngine(CarEngine engine) {
        carEngineRepository.save(engine);
    }
}
