package servicebook.user;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import servicebook.user.confirmation.EmailConfirmation;

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

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private EmailConfirmation emailConfirmation;
}
