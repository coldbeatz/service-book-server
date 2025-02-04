package servicebook.services;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import servicebook.entity.CarBrand;

import servicebook.exceptions.DuplicateEntityException;
import servicebook.repository.CarBrandRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarBrandService {

    private final CarBrandRepository carBrandRepository;

    @Transactional
    public void saveOrUpdate(CarBrand brand) {
        Optional<CarBrand> findBrandOptional = carBrandRepository.findBrandByName(brand.getBrand());

        if (findBrandOptional.isPresent() && findBrandOptional.get().getId() != brand.getId()) {
            throw new DuplicateEntityException("car_brand_not_unique");
        }

        carBrandRepository.save(brand);
    }

    @Transactional(readOnly = true)
    public CarBrand findById(Long id) {
        return carBrandRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<CarBrand> getAll() {
        return carBrandRepository.findAll();
    }
}
