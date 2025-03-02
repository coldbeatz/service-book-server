package servicebook.services;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import servicebook.entity.Car;

import servicebook.repository.CarRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    @Transactional(readOnly = true)
    public List<Car> getAll() {
        return carRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Car getCarById(Long id) {
        return carRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<Car> getCarsByBrand(Long brandId) {
        return carRepository.findCarsByBrand(brandId);
    }

    @Transactional
    public void saveOrUpdate(Car car) {
        carRepository.save(car);
    }
}
