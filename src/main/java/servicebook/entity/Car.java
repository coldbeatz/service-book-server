package servicebook.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import servicebook.entity.engine.CarEngine;
import servicebook.resources.Resource;
import servicebook.user.User;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    /**
     * Марка автомобіля
     */
    @ManyToOne
    @JoinColumn(name = "brand", nullable = false)
    private CarBrand brand;

    /**
     * Назва моделі автомобіля
     */
    @Column(name = "model", nullable = false)
    private String model;

    /**
     * Рік початку випуску автомобіля
     */
    @Column(name = "start_year", nullable = false)
    private int startYear;

    /**
     * Рік кінця випуску автомобіля
     */
    @Column(name = "end_year")
    private Integer endYear;

    /**
     * Ресурс зображення
     */
    @OneToOne
    @JoinColumn(name = "image_resource")
    private Resource imageResource;

    /**
     * Користувач, який додав автомобіль
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Двигуни автомобіля, що були у випуску
     */
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarEngine> engines;

    /**
     * Трансмісії автомобіля, які були у випуску
     */
    @ElementCollection
    @CollectionTable(name = "car_transmissions", joinColumns = @JoinColumn(name = "car_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "transmission")
    private List<CarTransmissionType> transmissions = new ArrayList<>();
}
