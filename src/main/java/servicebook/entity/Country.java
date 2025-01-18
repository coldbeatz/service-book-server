package servicebook.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import servicebook.localization.LocalizedString;
import servicebook.resources.Resource;

@Getter
@Setter
@ToString
@Entity(name = "countries")
@Table(indexes = {
    @Index(columnList = "code")
})
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    /**
     * Код країни: UA, EN, ...
     */
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    /**
     * Локалізована назва країни
     */
    @OneToOne
    @JoinColumn(name = "name_id")
    private LocalizedString name;

    /**
     * Ресурс іконки (використовується на сайті)
     */
    @OneToOne
    @JoinColumn(name = "icon_resource")
    private Resource iconResource;
}