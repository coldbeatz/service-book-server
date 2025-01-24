package servicebook.entity.engine;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import servicebook.entity.Car;
import servicebook.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "car_engines")
public class CarEngine {

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
     * Користувач, який додав двигун
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "created_by", updatable = false)
    private User createdBy;

    /**
     * Користувач, який змінив двигун
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Автомобіль
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;
}
