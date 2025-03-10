package servicebook.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import servicebook.entity.Car;

import servicebook.entity.maintenance.RegulationsMaintenance;
import servicebook.services.CarService;
import servicebook.services.RegulationsMaintenanceService;

import java.util.List;

@RestController
@RequestMapping("admin/cars/{carId}/maintenance")
@RequiredArgsConstructor
public class CarRegulationsMaintenanceController {

    private final CarService carService;
    private final RegulationsMaintenanceService maintenanceService;

    @GetMapping("/default")
    public ResponseEntity<List<RegulationsMaintenance>> loadDefaultMaintenances(@PathVariable("carId") Long carId) {
        Car car = carService.getCarById(carId);
        maintenanceService.ensureDefaultMaintenancesLoaded(car);

        return ResponseEntity.ok(car.getMaintenances());
    }

    @DeleteMapping
    public ResponseEntity<?> clearAll(@PathVariable("carId") Long carId) {
        Car car = carService.getCarById(carId);
        maintenanceService.deleteAllByCar(car);
        car.getMaintenances().clear();

        return ResponseEntity.noContent().build();
    }
}
