package servicebook.controllers.car;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import servicebook.controllers.BaseController;
import servicebook.entity.Car;
import servicebook.entity.CarBrand;

import servicebook.requests.CarRequest;
import servicebook.resources.Resource;

import servicebook.services.CarBrandService;
import servicebook.services.CarService;
import servicebook.services.upload.FileUploadService;

import servicebook.user.User;

import java.time.LocalDateTime;

import java.util.List;

/**
 * REST-контролер для керування автомобілями.
 * Доступний лише адміністраторам
 */
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController extends BaseController {

    private final CarService carService;
    private final CarBrandService carBrandService;

    private final FileUploadService fileUploadService;

    /**
     * Видалення автомобіля за ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        Car car = carService.getCarById(id);

        carService.delete(car);
        return ResponseEntity.noContent().build();
    }

    /**
     * Отримання автомобіля за ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Car> findById(@PathVariable("id") Long id) {
        Car car = carService.getCarById(id);

        return ResponseEntity.ok(car);
    }

    /**
     * Отримання списку автомобілів за ідентифікатором бренду
     */
    @GetMapping
    public ResponseEntity<List<Car>> getAll(@RequestParam(value = "brandId", required = false) Long brandId) {
        List<Car> cars = brandId != null ?
                carService.getCarsByBrand(brandId) :
                carService.getAll();

        return ResponseEntity.ok(cars);
    }

    /**
     * Додавання нового автомобіля
     */
    @PostMapping
    public ResponseEntity<Car> save(@ModelAttribute CarRequest request) {
        CarBrand brand = carBrandService.findById(request.getBrandId());

        Car car = new Car();

        car.setCreatedBy(getAuthenticatedUser());
        car.setBrand(brand);

        saveOrUpdateCar(car, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(car);
    }

    /**
     * Оновлення автомобіля
     */
    @PutMapping("/{id}")
    public ResponseEntity<Car> update(@PathVariable("id") Long id, @ModelAttribute CarRequest request) {
        Car car = carService.getCarById(id);

        car.setUpdatedBy(getAuthenticatedUser());
        car.setUpdatedAt(LocalDateTime.now());

        saveOrUpdateCar(car, request);
        return ResponseEntity.ok(car);
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
