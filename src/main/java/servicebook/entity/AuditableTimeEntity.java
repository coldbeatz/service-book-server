package servicebook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Абстрактний клас для збереження часу створення та оновлення запису
 */
@Getter
@Setter
@MappedSuperclass
public class AuditableTimeEntity {

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
