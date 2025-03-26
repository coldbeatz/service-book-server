package servicebook.entity;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;

import lombok.*;

/**
 * Нотатка про автомобіль (запис "сервісної книги")
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_cars_notes")
public class UserCarNote extends AuditableTimeEntity {

    /**
     * Унікальний ідентифікатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Автомобіль користувача
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_car_id", nullable = false)
    private UserCar userCar;

    /**
     * Короткий опис запису
     */
    @NotBlank
    @Column(name = "short_description", nullable = false, length = 30)
    private String shortDescription;

    /**
     * Короткий опис обслуговування
     */
    @NotBlank
    @Column(name = "content", nullable = false, length = 65535, columnDefinition = "TEXT")
    private String content;
}
