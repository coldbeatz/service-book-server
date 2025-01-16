package servicebook.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import servicebook.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "user_tokens")
public class UserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    /**
     * Зв'язок з користувачем системи
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Сам токен
     */
    @Column(name = "token", nullable = false, unique = true, length = 512)
    private String token;

    /**
     * Час закінчення токена
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Прапор активності токена
     */
    @Column(name = "active", nullable = false)
    private boolean active;
}
