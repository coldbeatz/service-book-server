package servicebook.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
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

/**
 * Контролер для керування регламентними обслуговуваннями.
 * Дозволяє створювати, оновлювати, отримувати та видаляти обслуговування.
 */
@RestController
@RequestMapping("regulations_maintenance")
@RequiredArgsConstructor
public class RegulationsMaintenanceController extends BaseController {

    private final RegulationsMaintenanceService maintenanceService;

    /**
     * Видаляє регламентне обслуговування за ID.
     * Доступно лише адміністраторам.
     *
     * @param id ID обслуговування
     * @return 204 No Content
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        RegulationsMaintenance maintenance = maintenanceService.getById(id);
        maintenanceService.delete(maintenance);

        return ResponseEntity.noContent().build();
    }

    /**
     * Повертає список усіх стандартних регламентних обслуговувань.
     * Доступно як користувачам, так і адміністраторам.
     *
     * @return список регламентів
     */
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<RegulationsMaintenance>> all() {
        List<RegulationsMaintenance> maintenances = maintenanceService.getDefaultMaintenances();

        return ResponseEntity.ok(maintenances);
    }

    /**
     * Створює нове регламентне обслуговування.
     * Доступно лише адміністраторам.
     *
     * @param request запит з даними про обслуговування
     * @return створене обслуговування
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<RegulationsMaintenance> save(@RequestBody RegulationsMaintenanceRequest request) {
        RegulationsMaintenance maintenance = new RegulationsMaintenance();

        maintenance.setWorkDescription(new LocalizedString());
        maintenance.setCreatedBy(getAuthenticatedUser());

        saveOrUpdateRegulationsMaintenance(request, maintenance);
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenance);
    }

    /**
     * Оновлює існуюче регламентне обслуговування.
     * Доступно лише адміністраторам.
     *
     * @param id ID обслуговування
     * @param request запит з оновленими даними
     * @return оновлене обслуговування
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RegulationsMaintenance> update(@PathVariable Long id,
                                                         @RequestBody RegulationsMaintenanceRequest request) {

        RegulationsMaintenance maintenance = maintenanceService.getById(id);

        maintenance.setUpdatedBy(getAuthenticatedUser());

        saveOrUpdateRegulationsMaintenance(request, maintenance);
        return ResponseEntity.ok(maintenance);
    }

    /**
     * Метод для заповнення та збереження регламентного обслуговування.
     * Також синхронізує список завдань обслуговування.
     *
     * @param request DTO з оновленими або новими даними
     * @param maintenance об'єкт, що зберігається або оновлюється
     */
    private void saveOrUpdateRegulationsMaintenance(RegulationsMaintenanceRequest request,
                                                    RegulationsMaintenance maintenance) {

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

        maintenanceService.saveOrUpdate(maintenance);
    }
}
