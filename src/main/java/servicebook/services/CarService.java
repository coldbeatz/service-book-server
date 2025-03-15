package servicebook.services;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import servicebook.entity.Car;

import servicebook.exceptions.EntityHasDependenciesException;
import servicebook.repository.CarRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    @Transactional
    public void delete(Car car) {
        long dependencies = Optional.ofNullable(carRepository.hasDependencies(car)).orElse(0L);

        if (dependencies > 0) {
            throw new EntityHasDependenciesException("The car can't be delete, it has dependencies");
        }

        carRepository.delete(car);
    }

    @Transactional(readOnly = true)
    public long getCarsCountByBrandId(Long brandId) {
        return carRepository.countCarsByBrandId(brandId);
    }

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
