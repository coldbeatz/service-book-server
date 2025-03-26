package servicebook.controllers.car;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import servicebook.controllers.BaseController;

import servicebook.entity.Car;
import servicebook.entity.engine.CarEngine;

import servicebook.requests.EngineRequest;

import servicebook.services.CarEngineService;
import servicebook.services.CarService;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * REST-контролер для керування двигунами автомобіля.
 * <p>
 * Доступний адміністраторам (шлях /cars/{carId}/engines).
 * Дозволяє перегляд, створення, оновлення та видалення двигунів,
 * які належать певному автомобілю.
 * <p>
 * Для всіх запитів перевіряється відповідність двигуна до вказаного carId,
 * щоб уникнути несанкціонованого доступу.
 */
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/cars/{carId}/engines")
@RequiredArgsConstructor
public class CarEngineController extends BaseController {

    private final CarService carService;
    private final CarEngineService carEngineService;

    /**
     * Перевірка коректності доступу до автомобіля
     */
    private void accessValidation(CarEngine engine, Long carId) {
        if (!Objects.equals(engine.getCar().getId(), carId)) {
            throw new AccessDeniedException("You do not have permission to access this car");
        }
    }

    /**
     * Отримання двигуна автомобіля за ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarEngine> findById(@PathVariable Long carId, @PathVariable("id") Long id) {
        CarEngine engine = carEngineService.getEngineById(id);
        accessValidation(engine, carId);

        return ResponseEntity.ok(engine);
    }

    /**
     * Видалення двигуна автомобіля за ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long carId, @PathVariable Long id) {
        CarEngine engine = carEngineService.getEngineById(id);
        accessValidation(engine, carId);

        carEngineService.delete(engine);

        return ResponseEntity.noContent().build();
    }

    /**
     * Додавання нового двигуна автомобіля
     */
    @PostMapping
    public ResponseEntity<CarEngine> createEngine(@PathVariable Long carId, @RequestBody EngineRequest request) {
        Car car = carService.getCarById(carId);

        CarEngine engine = new CarEngine();

        engine.setCar(car);
        engine.setCreatedBy(getAuthenticatedUser());

        saveOrUpdate(request, engine);
        return ResponseEntity.status(HttpStatus.CREATED).body(engine);
    }

    /**
     * Оновлення двигуна автомобіля
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEngine(@PathVariable Long carId, @PathVariable Long id,
                                          @RequestBody EngineRequest request) {

        CarEngine engine = carEngineService.getEngineById(id);
        accessValidation(engine, carId);

        engine.setUpdatedBy(getAuthenticatedUser());
        engine.setUpdatedAt(LocalDateTime.now());

        saveOrUpdate(request, engine);
        return ResponseEntity.ok(engine);
    }

    private void saveOrUpdate(EngineRequest request, CarEngine engine) {
        engine.setName(request.getName());
        engine.setDisplacement(request.getDisplacement());
        engine.setHorsepower(request.getHorsepower());
        engine.setFuelType(request.getFuelType());

        carEngineService.saveOrUpdate(engine);
    }
}
