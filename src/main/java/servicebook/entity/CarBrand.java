package servicebook.entity;

import jakarta.persistence.*;

import lombok.*;

import servicebook.resources.Resource;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {
    @Index(columnList = "brand")
}, name = "car_brands")

public class CarBrand extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    /**
     * Марка автомобіля
     */
    @Column(name = "brand", nullable = false, unique = true)
    private String brand;

    /**
     * Країна
     */
    @ManyToOne
    @JoinColumn(name = "country")
    private Country country;

    /**
     * Ресурс зображення
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_resource")
    private Resource imageResource;
}