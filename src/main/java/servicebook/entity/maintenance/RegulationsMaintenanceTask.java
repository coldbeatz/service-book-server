package servicebook.entity.maintenance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.*;

/**
 * Завдання (операція) регламентного обслуговування, яка описує тип роботи, інтервал і специфічний пробіг
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "regulations_maintenance_tasks")
public class RegulationsMaintenanceTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Технічне обслуговування інтервальне в кілометрах
     */
    @Column(name = "interval_km")
    private int interval;

    /**
     * Технічне обслуговування при визначеному пробігу в кілометрах
     */
    @Column(name = "specific_mileage_km")
    private int specificMileage;

    /**
     * Тип роботи (заміна, перевірка)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "work_type", nullable = false)
    private MaintenanceWorkType workType;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "regulations_maintenance_id", nullable = false)
    private RegulationsMaintenance regulationsMaintenance;
}
