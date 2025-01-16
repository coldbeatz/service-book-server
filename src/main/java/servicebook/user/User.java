package servicebook.user;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import servicebook.user.confirmation.EmailConfirmation;
import servicebook.user.role.Role;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Електронна адреса при реєстрації
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Чи підтверджено e-mail
     */
    @Column(name = "confirm_email", nullable = false)
    private boolean confirmEmail;

    /**
     * ФІО користувача
     */
    @Column(name = "full_name")
    private String fullName;

    /**
     * Хеш пароля BCrypt
     */
    @Column(name = "password_hash")
    private String password;

    /**
     * Тип (роль) користувача
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private EmailConfirmation emailConfirmation;
}
