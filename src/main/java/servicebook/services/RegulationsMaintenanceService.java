package servicebook.services;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
