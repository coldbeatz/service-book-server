package servicebook.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.thymeleaf.util.StringUtils;

import servicebook.entity.maintenance.RegulationsMaintenance;

import servicebook.entity.maintenance.RegulationsMaintenanceTask;
import servicebook.localization.LocalizedString;

import servicebook.requests.LocalizedRequest;
import servicebook.requests.RegulationsMaintenanceRequest;
import servicebook.requests.RegulationsMaintenanceTaskRequest;

import servicebook.services.RegulationsMaintenanceService;

import java.util.List;

@RestController
@RequestMapping("admin/regulations_maintenance")
@RequiredArgsConstructor
public class RegulationsMaintenanceController extends BaseController {

    private final RegulationsMaintenanceService maintenanceService;

    @GetMapping
    public ResponseEntity<List<RegulationsMaintenance>> all() {
        List<RegulationsMaintenance> maintenances = maintenanceService.getAll();

        return ResponseEntity.ok(maintenances);
    }

    @PostMapping
    public ResponseEntity<RegulationsMaintenance> save(
            @RequestBody RegulationsMaintenanceRequest request) {

        RegulationsMaintenance maintenance = new RegulationsMaintenance();

        maintenance.setWorkDescription(new LocalizedString());
        maintenance.setCreatedBy(getAuthenticatedUser());

        buildRegulationsMaintenance(request, maintenance);

        maintenanceService.saveOrUpdate(maintenance);

        return ResponseEntity.ok(maintenance);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegulationsMaintenance> update(
            @PathVariable Long id, @RequestBody RegulationsMaintenanceRequest request) {

        RegulationsMaintenance maintenance = maintenanceService.getById(id);
        buildRegulationsMaintenance(request, maintenance);

        maintenance.setUpdatedBy(getAuthenticatedUser());

        maintenanceService.saveOrUpdate(maintenance);

        return ResponseEntity.ok(maintenance);
    }

    private void buildRegulationsMaintenance(RegulationsMaintenanceRequest request, RegulationsMaintenance maintenance) {
        LocalizedRequest localizedRequest = request.getWorkDescription();

        String en = localizedRequest.getEn();
        String ua = localizedRequest.getUa();

        if (StringUtils.isEmpty(en))
            throw new NullPointerException();

        if (StringUtils.isEmpty(ua))
            throw new NullPointerException();

        LocalizedString workDescription = maintenance.getWorkDescription();
        workDescription.update(en, ua);

        maintenance.setUseDefault(request.isUseDefault());

        maintenance.setTransmissions(request.getTransmissions());
        maintenance.setFuelTypes(request.getFuelTypes());

        List<Long> requestTaskIds = request.getTasks().stream()
                .map(RegulationsMaintenanceTaskRequest::getId)
                .filter(id -> id != 0)
                .toList();

        List<RegulationsMaintenanceTask> tasksToDelete = maintenance.getTasks()
                .stream().filter(task -> !requestTaskIds.contains(task.getId()))
                .toList();

        tasksToDelete.forEach(maintenanceService::deleteTask);

        for (RegulationsMaintenanceTaskRequest taskRequest : request.getTasks()) {
            RegulationsMaintenanceTask task;
            if (taskRequest.getId() != 0) {
                task = maintenance.getTask(taskRequest.getId());
            } else {
                task = new RegulationsMaintenanceTask();
                maintenance.addTask(task);
            }

            task.setInterval(taskRequest.getInterval());
            task.setSpecificMileage(taskRequest.getSpecificMileage());
            task.setWorkType(taskRequest.getWorkType());
        }
    }
}
