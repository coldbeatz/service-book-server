package servicebook.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import servicebook.entity.Car;
import servicebook.entity.engine.CarEngine;

import servicebook.requests.EngineRequest;

import servicebook.services.CarEngineService;
import servicebook.services.CarService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("admin/cars/{carId}/engines")
@RequiredArgsConstructor
public class CarEngineController extends BaseController {

    private final CarService carService;
    private final CarEngineService carEngineService;

    @GetMapping("/{id}")
    public ResponseEntity<CarEngine> findById(@PathVariable long carId, @PathVariable("id") Long id) {
        CarEngine engine = carEngineService.getEngineById(id);

        if (engine.getCar() == null) {
            return ResponseEntity.notFound().build();
        }

        if (engine.getCar().getId() != carId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        return ResponseEntity.ok(engine);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long carId, @PathVariable Long id) {
        CarEngine engine = carEngineService.getEngineById(id);

        if (engine.getCar().getId() != carId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        carEngineService.delete(engine);

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<CarEngine> createEngine(@PathVariable long carId, @RequestBody EngineRequest request) {
        Car car = carService.getCarById(carId);

        CarEngine engine = new CarEngine();
        engine.setCar(car);

        buildEngine(request, engine);

        engine.setCreatedBy(getAuthenticatedUser());
        carEngineService.saveOrUpdate(engine);

        return ResponseEntity.status(HttpStatus.CREATED).body(engine);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEngine(@PathVariable long carId, @PathVariable Long id,
                                          @RequestBody EngineRequest request) {

        CarEngine engine = carEngineService.getEngineById(id);

        if (engine.getCar().getId() != carId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        buildEngine(request, engine);

        engine.setUpdatedBy(getAuthenticatedUser());
        engine.setUpdatedAt(LocalDateTime.now());

        carEngineService.saveOrUpdate(engine);

        return ResponseEntity.ok(engine);
    }

    private void buildEngine(EngineRequest request, CarEngine engine) {
        engine.setName(request.getName());
        engine.setDisplacement(request.getDisplacement());
        engine.setHorsepower(request.getHorsepower());
        engine.setFuelType(request.getFuelType());
    }
}
