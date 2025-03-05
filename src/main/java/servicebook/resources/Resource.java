package servicebook.resources;

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
@Entity
@Table(name = "resources")
public class Resource {

    @JsonIgnore
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
     * Тип ресурса
     * TODO
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ResourceType type;

    /**
     * Дата завантаження ресурса
     */
    @JsonIgnore
    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    /**
     * Користувач, який завантажив ресурс
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public static Resource createResource(String fileURL, User user) {
        Resource resource = new Resource();

        resource.setUrl(fileURL);
        resource.setUser(user);
        resource.setUploadDate(LocalDateTime.now());

        return resource;
    }
}
