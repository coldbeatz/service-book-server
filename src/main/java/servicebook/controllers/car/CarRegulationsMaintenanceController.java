package servicebook.controllers.car;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import servicebook.entity.Car;

import servicebook.entity.maintenance.RegulationsMaintenance;

import servicebook.services.CarService;
import servicebook.services.RegulationsMaintenanceService;

import java.util.List;

/**
 * REST-контролер для керування регламентними обслуговуваннями (maintenance) автомобілів.
 * Доступний лише адміністраторам
 */
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("cars/{carId}/maintenance")
@RequiredArgsConstructor
public class CarRegulationsMaintenanceController {

    private final CarService carService;

    private final RegulationsMaintenanceService maintenanceService;

    /**
     * Завантажує стандартні (дефолтні) регламентні обслуговування для конкретного автомобіля.
     * Якщо обслуговування ще не створене — створюється автоматично.
     *
     * @param carId ID автомобіля
     * @return Список регламентних обслуговувань для цього автомобіля
     */
    @GetMapping("/default")
    public ResponseEntity<List<RegulationsMaintenance>> loadDefaultMaintenances(@PathVariable("carId") Long carId) {
        Car car = carService.getCarById(carId);

        maintenanceService.ensureDefaultMaintenancesLoaded(car);

        return ResponseEntity.ok(car.getMaintenances());
    }

    /**
     * Видаляє всі регламентні обслуговування, прив'язані до певного автомобіля
     *
     * @param carId ID автомобіля
     * @return 204 No Content, якщо успішно видалено
     */
    @DeleteMapping
    public ResponseEntity<?> clearAll(@PathVariable("carId") Long carId) {
        Car car = carService.getCarById(carId);

        maintenanceService.deleteAllByCar(car);
        car.getMaintenances().clear();

        return ResponseEntity.noContent().build();
    }
}
