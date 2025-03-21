package servicebook.entity.engine;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.*;

import servicebook.entity.AuditableEntity;
import servicebook.entity.Car;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "car_engines")
public class CarEngine extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    /**
     * Назва двигуна
     */
    @Column(name = "name", nullable = true)
    private String name;

    /**
     * Об'єм двигуна
     */
    @Column(name = "displacement", nullable = false)
    private double displacement;

    /**
     * Тип палива (напр. бензин, дизель, електро)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false)
    private FuelType fuelType;

    /**
     * Потужність двигуна (к.с.)
     */
    @Column(name = "horsepower", nullable = false)
    private int horsepower;

    /**
     * Автомобіль
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;
}
