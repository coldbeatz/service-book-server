package servicebook.services;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import servicebook.entity.Car;
import servicebook.entity.maintenance.RegulationsMaintenance;

import servicebook.entity.maintenance.RegulationsMaintenanceTask;

import servicebook.repository.RegulationsMaintenanceRepository;
import servicebook.repository.RegulationsMaintenanceTaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegulationsMaintenanceService {

    private final RegulationsMaintenanceRepository maintenanceRepository;
    private final RegulationsMaintenanceTaskRepository taskRepository;

    @Transactional
    public void delete(RegulationsMaintenance maintenance) {
        maintenanceRepository.delete(maintenance);
    }

    @Transactional
    public void deleteAllByCar(Car car) {
        maintenanceRepository.deleteByCar(car);
    }

    @Transactional
    public void ensureDefaultMaintenancesLoaded(Car car) {
        List<RegulationsMaintenance> maintenances = car.getMaintenances();

        if (maintenances.isEmpty()) {
            List<RegulationsMaintenance> defaultMaintenances = getDefaultMaintenances();
            List<RegulationsMaintenance> clones = defaultMaintenances.stream()
                    .map(RegulationsMaintenance::copyWithoutId)
                    .toList();

            for (var clone : clones) {
                car.addMaintenance(clone);
                saveOrUpdate(clone);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<RegulationsMaintenance> getDefaultMaintenances() {
        return maintenanceRepository.getDefaultMaintenances();
    }

    @Transactional(readOnly = true)
    public List<RegulationsMaintenance> findByCarId(Long carId) {
        return maintenanceRepository.findByCarId(carId);
    }

    @Transactional(readOnly = true)
    public List<RegulationsMaintenance> findByCar(Car car) {
        return maintenanceRepository.findByCar(car);
    }

    @Transactional(readOnly = true)
    public List<RegulationsMaintenance> getAll() {
        return maintenanceRepository.findAll();
    }

    @Transactional
    public void saveOrUpdate(RegulationsMaintenance maintenance) {
        maintenanceRepository.save(maintenance);
    }

    @Transactional(readOnly = true)
    public RegulationsMaintenance getById(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void deleteTask(RegulationsMaintenanceTask task) {
        RegulationsMaintenance maintenance = task.getRegulationsMaintenance();

        if (maintenance != null) {
            maintenance.getTasks().remove(task);
            task.setRegulationsMaintenance(null);
        }

        taskRepository.delete(task);
    }
}
