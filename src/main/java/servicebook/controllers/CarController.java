package servicebook.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import servicebook.entity.Car;
import servicebook.entity.CarBrand;
import servicebook.entity.CarTransmissionType;

import servicebook.repository.CarBrandRepository;

import servicebook.resources.Resource;

import servicebook.services.CarService;
import servicebook.services.upload.FileUploadService;

import servicebook.user.User;

import servicebook.utils.responce.ResponseUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("admin/cars")
@RequiredArgsConstructor
public class CarController extends BaseController {

    private final CarService carService;
    private final CarBrandRepository carBrandRepository;

    private final FileUploadService fileUploadService;

    @GetMapping("/checkModelName")
    public ResponseEntity<?> checkModelName() {
        try {
            // TODO
            return ResponseUtil.success(CarTransmissionType.values());
        } catch (Exception e) {
            return ResponseUtil.error();
        }
    }

    @GetMapping("/transmissions")
    public ResponseEntity<?> getAvailableTransmissions() {
        return ResponseUtil.success(CarTransmissionType.values());
    }

    @GetMapping("car")
    public ResponseEntity<Car> getCarById(@RequestParam("id") Long carId) {
        Car car = carService.getCarById(carId);

        return ResponseUtil.success(car);
    }

    @GetMapping
    public ResponseEntity<List<Car>> getCarsByBrand(@RequestParam("brand") Long brandId) {
        List<Car> cars = carService.getCarsByBrand(brandId);

        return ResponseUtil.success(cars);
    }

    @PostMapping("/{brandId}")
    public ResponseEntity<?> createCar(@PathVariable("brandId") Long brandId,
                                       @RequestParam("model") String model,
                                       @RequestParam("startYear") Integer startYear,
                                       @RequestParam(value = "endYear", required = false) Integer endYear,
                                       @RequestParam("file") MultipartFile file,
                                       @RequestParam("transmissions") List<String> transmissions) {

        Optional<CarBrand> brandOptional = carBrandRepository.findById(brandId);
        if (brandOptional.isEmpty()) {
            return ResponseUtil.error("car_brand_not_found");
        }

        try {
            CarBrand brand = brandOptional.get();

            Car car = new Car();
            createOrUpdateCar(car, brand, model, startYear, endYear, file, transmissions);

            return ResponseEntity.ok(car);
        } catch (Exception e) {
            return ResponseUtil.error();
        }
    }

    @PostMapping("/{brandId}/{carId}")
    public ResponseEntity<?> updateCar(@PathVariable("brandId") Long brandId,
                                       @PathVariable("carId") Long carId,
                                       @RequestParam("model") String model,
                                       @RequestParam("startYear") Integer startYear,
                                       @RequestParam(value = "endYear", required = false) Integer endYear,
                                       @RequestParam(value = "file", required = false) MultipartFile file,
                                       @RequestParam("transmissions") List<String> transmissions) {

        Car car = carService.getCarById(carId);
        if (car == null) {
            return ResponseUtil.error("car_not_found");
        }

        try {
            createOrUpdateCar(car, null, model, startYear, endYear, file, transmissions);
            return ResponseEntity.ok(car);
        } catch (Exception e) {
            return ResponseUtil.error();
        }
    }

    private void createOrUpdateCar(Car car,
                                   CarBrand carBrand,
                                   String model,
                                   Integer startYear,
                                   Integer endYear,
                                   MultipartFile file,
                                   List<String> transmissions) throws IOException {

        User user = getAuthenticatedUser();

        if (file != null && !file.isEmpty()) {
            Resource resource = fileUploadService.uploadFileToExternalHost(file, user, car.getImageResource());
            car.setImageResource(resource);
        } else {
            if (carBrand != null) {
                throw new NullPointerException("The image file cannot be empty");
            }
        }

        if (carBrand != null) {
            car.setBrand(carBrand);
        }

        car.setModel(model);
        car.setUser(user);
        car.setStartYear(startYear);
        car.setEndYear(endYear);

        List<CarTransmissionType> transmissionsList = transmissions.stream()
                .map(CarTransmissionType::valueOf)
                .toList();

        car.setTransmissions(new ArrayList<>(transmissionsList));

        carService.saveCar(car);
    }
}
