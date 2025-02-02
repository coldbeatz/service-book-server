package servicebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import servicebook.entity.maintenance.RegulationsMaintenance;

public interface RegulationsMaintenanceRepository extends JpaRepository<RegulationsMaintenance, Long> {

}
