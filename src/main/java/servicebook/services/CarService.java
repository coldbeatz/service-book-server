package servicebook.services;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import servicebook.entity.Car;

import servicebook.repository.CarRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    public Car getCarById(Long id) {
        return carRepository.findById(id).orElse(null);
    }

    public List<Car> getCarsByBrand(Long brandId) {
        return carRepository.findCarsByBrand(brandId);
    }

    public void saveCar(Car car) {
        carRepository.save(car);
    }
}
