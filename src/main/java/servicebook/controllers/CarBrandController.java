package servicebook.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import servicebook.entity.CarBrand;

import servicebook.repository.CarBrandRepository;

import servicebook.utils.responce.ErrorResponse;

import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/brands")
public class CarBrandController {

    private final CarBrandRepository carBrandRepository;

    @Autowired
    public CarBrandController(CarBrandRepository carBrandRepository) {
        this.carBrandRepository = carBrandRepository;
    }

    @GetMapping("/all")
    public ResponseEntity<?> all() {
        try {
            List<CarBrand> carBrands = StreamSupport
                    .stream(carBrandRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(carBrands);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse());
        }
    }
}
