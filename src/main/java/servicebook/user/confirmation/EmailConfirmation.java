package servicebook.user.confirmation;

import jakarta.persistence.*;

import lombok.AccessLevel;
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

    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "unique_key", nullable = false, unique = true)
    private String uniqueKey;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;
}
