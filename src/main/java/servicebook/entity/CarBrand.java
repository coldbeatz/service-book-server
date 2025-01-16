package servicebook.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity(name = "car_brands")
public class CarBrand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    /**
     * Марка автомобіля
     */
    @Column(name = "brand")
    private String brand;

    /**
     * Країна
     */
    @ManyToOne
    @JoinColumn(name = "country")
    private Country country;

}