package servicebook.controllers.user;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import servicebook.controllers.BaseController;

import servicebook.entity.Car;
import servicebook.entity.UserCar;
import servicebook.entity.engine.CarEngine;

import servicebook.requests.UserCarRequest;
import servicebook.resources.Resource;

import servicebook.services.CarEngineService;
import servicebook.services.CarService;
import servicebook.services.UserCarService;

import servicebook.services.upload.FileUploadService;

import servicebook.user.User;

import java.time.LocalDateTime;

import java.util.List;

/**
 * REST-контролер для керування автомобілями користувача.
 * Доступно авторизованим користувачам
 */
@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/user/cars")
@RequiredArgsConstructor
public class UserCarController extends BaseController {

    private final UserCarService userCarService;

    private final CarService carService;
    private final CarEngineService carEngineService;

    private final FileUploadService fileUploadService;

    /**
     * Перевірка чи користувач має доступ до автомобіля
     */
    private void accessValidation(UserCar userCar) {
        User creator = userCar.getUser();
        User authUser = getAuthenticatedUser();

        if (creator.getId() != authUser.getId()) {
            throw new AccessDeniedException("You do not have permission to access this car");
        }
    }

    /**
     * Отримання конкретного автомобіля користувача за ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserCar> getUserCarById(@PathVariable Long id) {
        UserCar userCar = userCarService.getById(id);
        accessValidation(userCar);

        return ResponseEntity.ok(userCar);
    }

    /**
     * Отримання списку всіх автомобілів поточного авторизованого користувача
     */
    @GetMapping
    public ResponseEntity<List<UserCar>> getUserCars() {
        List<UserCar> userCars = userCarService.getAllByUser(getAuthenticatedUser());

        return ResponseEntity.ok(userCars);
    }

    /**
     * Видалення автомобіля користувача за ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UserCar userCar = userCarService.getById(id);

        accessValidation(userCar);
        userCarService.delete(userCar);

        return ResponseEntity.noContent().build();
    }

    /**
     * Додавання нового автомобіля користувача
     */
    @PostMapping
    public ResponseEntity<UserCar> save(@ModelAttribute UserCarRequest request) {
        User user = getAuthenticatedUser();

        UserCar userCar = new UserCar();

        userCar.setUser(user);

        saveOrUpdate(userCar, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCar);
    }

    /**
     * Оновлення існуючого автомобіля користувача
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @ModelAttribute UserCarRequest request) {
        UserCar userCar = userCarService.getById(id);

        accessValidation(userCar);

        userCar.setUpdatedAt(LocalDateTime.now());

        saveOrUpdate(userCar, request);
        return ResponseEntity.ok(userCar);
    }

    /**
     * Загальний метод для збереження або оновлення автомобіля користувача
     */
    private void saveOrUpdate(UserCar userCar, UserCarRequest request) {
        User user = getAuthenticatedUser();
        MultipartFile file = request.getFile();

        if (file != null && !file.isEmpty()) {
            Resource resource = fileUploadService.uploadFileToExternalHost(file, user, userCar.getImageResource());
            userCar.setImageResource(resource);
        }

        Car car = carService.getCarById(request.getCarId());
        userCar.setCar(car);

        if (request.getEngineId() != null) {
            CarEngine engine = carEngineService.getEngineById(request.getEngineId());
            userCar.setEngine(engine);
        }

        userCar.setVehicleMileage(request.getVehicleMileage());
        userCar.setVehicleYear(request.getVehicleYear());

        userCar.setVinCode(request.getVinCode());
        userCar.setLicensePlate(request.getLicensePlate());

        userCar.setTransmissionType(request.getTransmissionType());
        userCar.setFuelType(request.getFuelType());

        userCarService.saveOrUpdate(userCar);
    }
}
