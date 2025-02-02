package servicebook.entity.maintenance;

import jakarta.persistence.*;

import lombok.*;

import servicebook.entity.AuditableEntity;
import servicebook.entity.CarTransmissionType;
import servicebook.entity.engine.FuelType;

import servicebook.entity.maintenance.converters.CarTransmissionTypeJsonConverter;
import servicebook.entity.maintenance.converters.FuelTypeJsonConverter;

import servicebook.localization.LocalizedString;

import java.util.ArrayList;
import java.util.List;

/**
 * Регламенте технічне обслуговування
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "regulations_maintenance")
public class RegulationsMaintenance extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    /**
     * Локалізований опис роботи регламентного обслуговування
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "work_description_id", nullable = false)
    private LocalizedString workDescription;

    /**
     * Використовувати за замовчуванням для кожного авто, якщо збігається трансмісія або тип палива
     */
    @Column(name = "use_default", nullable = false)
    private boolean useDefault;

    /**
     * Трансмісії які обслуговуються при даній роботі
     */
    @Column(name = "transmissions", columnDefinition = "json")
    @Convert(converter = CarTransmissionTypeJsonConverter.class)
    private List<CarTransmissionType> transmissions = new ArrayList<>();

    /**
     * Типи палива авто які обслуговуються при даній роботі
     */
    @Column(name = "fuel_types", columnDefinition = "json")
    @Convert(converter = FuelTypeJsonConverter.class)
    private List<FuelType> fuelTypes = new ArrayList<>();

    /**
     * Задачі (операції) регламентного обслуговування.
     * Один регламент може містити кілька завдань (наприклад, перевірка і заміна)
     */
    @OneToMany(mappedBy = "regulationsMaintenance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegulationsMaintenanceTask> tasks = new ArrayList<>();

    public void addTask(RegulationsMaintenanceTask task) {
        tasks.add(task);
        task.setRegulationsMaintenance(this);
    }

    public void removeTask(RegulationsMaintenanceTask task) {
        tasks.remove(task);
        task.setRegulationsMaintenance(null);
    }

    public RegulationsMaintenanceTask getTask(long taskId) {
        for (RegulationsMaintenanceTask task : tasks) {
            if (task.getId() == taskId) {
                return task;
            }
        }
        return null;
    }
}