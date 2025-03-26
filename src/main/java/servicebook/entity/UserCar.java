package servicebook.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import jakarta.validation.constraints.*;

import lombok.*;

import servicebook.entity.engine.CarEngine;
import servicebook.entity.engine.FuelType;

import servicebook.resources.Resource;

import servicebook.user.User;


/**
 * Автомобіль користувача
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_cars", indexes = {
    @Index(name = "idx_vin_code", columnList = "vin_code")
})
public class UserCar extends AuditableTimeEntity {

    /**
     * Унікальний ідентифікатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Автомобіль
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "car", nullable = false)
    private Car car;

    /**
     * Двигун автомобіля
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "engine")
    private CarEngine engine;

    /**
     * Рік випуску автомобіля
     */
    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(name = "vehicle_year", nullable = false)
    private int vehicleYear;

    /**
     * VIN-код автомобіля
     */
    @NotBlank
    @Size(min = 10, max = 20)
    @Column(name = "vin_code", nullable = false)
    private String vinCode;

    /**
     * Номерний знак автомобіля
     */
    @NotBlank
    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    /**
     * Тип трансмісії автомобіля (автомат, механіка, і т.д.)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "transmission_type", nullable = false)
    private CarTransmissionType transmissionType;

    /**
     * Тип палива (напр. бензин, дизель, електро)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false)
    private FuelType fuelType;

    /**
     * Пробіг автомобіля
     */
    @Min(0)
    @Column(name = "vehicle_mileage", nullable = false)
    private int vehicleMileage;

    /**
     * Ресурс зображення автомобіля користувача
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_resource")
    private Resource imageResource;

    /**
     * Користувач, який додав автомобіль
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Записи користувача до цього автомобіля
     */
    //@OneToMany(mappedBy = "userCar", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<UserCarNote> notes;
}