package servicebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import servicebook.entity.Car;

import servicebook.entity.maintenance.RegulationsMaintenance;

import java.util.List;

public interface RegulationsMaintenanceRepository extends JpaRepository<RegulationsMaintenance, Long> {

    void deleteByCar(Car car);

    List<RegulationsMaintenance> findByCar(Car car);

    @Query("SELECT r FROM RegulationsMaintenance r WHERE r.car.id = :carId")
    List<RegulationsMaintenance> findByCarId(@Param("carId") Long carId);

    @Query("SELECT r FROM RegulationsMaintenance r WHERE r.useDefault = TRUE AND r.car IS NULL")
    List<RegulationsMaintenance> getDefaultMaintenances();
}
