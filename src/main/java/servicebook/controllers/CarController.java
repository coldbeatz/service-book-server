package servicebook.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import servicebook.entity.Car;
import servicebook.entity.CarBrand;

import servicebook.requests.CarRequest;
import servicebook.resources.Resource;

import servicebook.services.CarBrandService;
import servicebook.services.CarService;
import servicebook.services.upload.FileUploadService;

import servicebook.user.User;

import servicebook.utils.responce.ResponseUtil;

import java.time.LocalDateTime;

import java.util.List;

@RestController
@RequestMapping("admin/cars")
@RequiredArgsConstructor
public class CarController extends BaseController {

    private final CarService carService;
    private final CarBrandService carBrandService;

    private final FileUploadService fileUploadService;

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        Car car = carService.getCarById(id);
        carService.delete(car);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> findById(@PathVariable("id") Long id) {
        Car car = carService.getCarById(id);

        return ResponseUtil.success(car);
    }

    @GetMapping
    public ResponseEntity<List<Car>> getAll(@RequestParam(value = "brandId", required = false) Long brandId) {
        List<Car> cars = brandId != null ?
                carService.getCarsByBrand(brandId) :
                carService.getAll();

        return ResponseUtil.success(cars);
    }

    @PostMapping
    public ResponseEntity<?> save(@ModelAttribute CarRequest request) {
        CarBrand brand = carBrandService.findById(request.getBrandId());
        User user = getAuthenticatedUser();

        try {
            Car car = new Car();

            car.setCreatedBy(user);
            car.setBrand(brand);

            saveOrUpdateCar(car, request);

            return ResponseEntity.ok(car);
        } catch (Exception e) {
            return ResponseUtil.error();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @ModelAttribute CarRequest request) {
        Car car = carService.getCarById(id);
        User user = getAuthenticatedUser();

        car.setUpdatedBy(user);
        car.setUpdatedAt(LocalDateTime.now());

        try {
            saveOrUpdateCar(car, request);

            return ResponseEntity.ok(car);
        } catch (Exception e) {
            return ResponseUtil.error();
        }
    }

    private void saveOrUpdateCar(Car car, CarRequest request) {
        User user = getAuthenticatedUser();
        MultipartFile file = request.getFile();

        if (file != null && !file.isEmpty()) {
            Resource resource = fileUploadService.uploadFileToExternalHost(file, user, car.getImageResource());
            car.setImageResource(resource);
        } else {
            if (car.getId() == 0) {
                throw new NullPointerException("The image file cannot be empty");
            }
        }

        car.setModel(request.getModel());
        car.setStartYear(request.getStartYear());
        car.setEndYear(request.getEndYear());
        car.setTransmissions(request.getTransmissions());

        carService.saveOrUpdate(car);
    }
}
