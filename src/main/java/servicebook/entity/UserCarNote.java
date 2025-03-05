package servicebook.entity;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_cars_notes")
public class UserCarNote {

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

    /**
     * Час створення запису
     */
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Час останнього оновлення запису
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
