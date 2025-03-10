package servicebook.user.confirmation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import servicebook.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity(name = "email_confirmations")
public class EmailConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    @ToString.Exclude
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "unique_key", nullable = false, unique = true)
    private String uniqueKey;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    /**
     * Бажаний e-mail, nullable тому-що при реєстрації це не потрібно, а при зміні - потрібно
     */
    @Column(name = "desired_email", unique = true)
    private String desiredEmail;
}
