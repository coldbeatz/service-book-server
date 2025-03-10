package servicebook.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import servicebook.entity.engine.CarEngine;

import servicebook.entity.maintenance.RegulationsMaintenance;
import servicebook.entity.maintenance.converters.CarTransmissionTypeJsonConverter;

import servicebook.resources.Resource;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "cars")
public class Car extends AuditableEntity {

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
     * Двигуни автомобіля, що були у випуску
     */
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarEngine> engines;

    /**
     * Трансмісії автомобіля, які були у випуску
     */
    @Column(name = "transmissions", columnDefinition = "json")
    @Convert(converter = CarTransmissionTypeJsonConverter.class)
    private List<CarTransmissionType> transmissions = new ArrayList<>();

    /**
     * Список регламентних обслуговувань, прив'язаних до цього автомобіля
     */
    @OneToMany(mappedBy = "car", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonManagedReference
    private List<RegulationsMaintenance> maintenances = new ArrayList<>();

    public void addMaintenance(RegulationsMaintenance maintenance) {
        maintenances.add(maintenance);
        maintenance.setCar(this);
    }

    public void removeMaintenance(RegulationsMaintenance maintenance) {
        maintenances.remove(maintenance);
        maintenance.setCar(null);
    }
}
