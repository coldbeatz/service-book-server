package servicebook.resources;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import servicebook.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    /**
     * URL до ресурса
     */
    @Column(name = "url")
    private String url;

    /**
     * Дата завантаження ресурса
     */
    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    /**
     * Користувач, який завантажив ресурс
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
