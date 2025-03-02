package servicebook.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import servicebook.entity.Car;
import servicebook.entity.engine.CarEngine;
import servicebook.entity.engine.FuelType;

import servicebook.services.CarEngineService;
import servicebook.services.CarService;

import servicebook.utils.responce.ResponseUtil;

@RestController
@RequestMapping("admin/engines")
@RequiredArgsConstructor
public class CarEngineController extends BaseController {

    private final CarService carService;
    private final CarEngineService carEngineService;

    @GetMapping("/fuel_types")
    public ResponseEntity<?> getAvailableFuelTypes() {
        return ResponseUtil.success(FuelType.values());
    }

    /*@GetMapping()
    public ResponseEntity<List<CarEngine>> getEnginesByCar(@RequestParam("car") Long carId) {
        Car car = carService.getCarById(carId);
        if (car != null) {
            return ResponseUtil.success(car.getEngines());
        }

        return ResponseUtil.success(Collections.emptyList());
    }*/

    @GetMapping("/{id}")
    public ResponseEntity<CarEngine> findById(@PathVariable("id") Long id) {
        CarEngine engine = carEngineService.getEngineById(id);

        return ResponseEntity.ok(engine);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateEngine(@RequestParam("engineId") Long engineId,
                                          @RequestParam(value = "name", required = false) String name,
                                          @RequestParam("displacement") Double displacement,
                                          @RequestParam("fuelType") String fuelType,
                                          @RequestParam("horsepower") Integer horsepower) {

        CarEngine engine = carEngineService.getEngineById(engineId);
        if (engine == null) {
            return ResponseUtil.error("car_engine_not_found");
        }

        try {
            FuelType fuelTypeEnum = FuelType.valueOf(fuelType);

            engine.setName(name);
            engine.setDisplacement(displacement);
            engine.setHorsepower(horsepower);
            engine.setFuelType(fuelTypeEnum);
            engine.setUpdatedBy(getAuthenticatedUser());

            carEngineService.saveEngine(engine);

            return ResponseEntity.ok(engine);
        } catch (Exception e) {
            return ResponseUtil.error();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEngine(@RequestParam("carId") Long carId,
                                          @RequestParam(value = "name", required = false) String name,
                                          @RequestParam("displacement") Float displacement,
                                          @RequestParam("fuelType") String fuelType,
                                          @RequestParam("horsepower") Integer horsepower) {

        Car car = carService.getCarById(carId);
        if (car == null) {
            return ResponseUtil.error("car_not_found");
        }

        try {
            FuelType fuelTypeEnum = FuelType.valueOf(fuelType);

            CarEngine engine = CarEngine.builder()
                    .car(car)
                    .name(name)
                    .displacement(displacement)
                    .horsepower(horsepower)
                    .fuelType(fuelTypeEnum)
                    .createdBy(getAuthenticatedUser())
                    .build();

            carEngineService.saveEngine(engine);

            return ResponseEntity.ok(engine);
        } catch (Exception e) {
            return ResponseUtil.error();
        }
    }
}
