package servicebook.entity.news;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import lombok.*;

import servicebook.entity.AuditableEntity;
import servicebook.entity.news.converters.NewsPostingOptionJsonConverter;

import servicebook.localization.LocalizedString;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "news")
public class News extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Локалізований заголовок новини
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "title_id", nullable = false)
    private LocalizedString title;

    /**
     * Локалізований контент новини
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "content_id", nullable = false)
    private LocalizedString content;

    /**
     * Дата відкладеного постингу новини
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "delayed_posting_date", nullable = false)
    private LocalDateTime delayedPostingDate;

    /**
     * Типи постингу новин
     */
    @Column(name = "posting_options", columnDefinition = "json", nullable = false)
    @Convert(converter = NewsPostingOptionJsonConverter.class)
    private List<NewsPostingOption> postingOptions = new ArrayList<>();

    /**
     * Позначка, що новина вже була опублікована (наприклад, надіслана email або розміщена на сайті)
     */
    @Column(name = "is_posted", nullable = false)
    private boolean posted = false;

    public boolean hasPostingOption(NewsPostingOption option) {
        return postingOptions.contains(option);
    }
}
