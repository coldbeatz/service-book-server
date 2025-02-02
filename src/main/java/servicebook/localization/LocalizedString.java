package servicebook.localization;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
@Entity(name = "localization")
public class LocalizedString {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    @Column(name = "en", length = 2048)
    private String en;

    @Column(name = "ua", length = 2048)
    private String ua;

    public static LocalizedString create(String en, String ua) {
        LocalizedString localizedString = new LocalizedString();

        localizedString.setEn(en);
        localizedString.setUa(ua);

        return localizedString;
    }

    public void update(String en, String ua) {
        this.en = en;
        this.ua = ua;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o instanceof LocalizedString value) {
            return Objects.equals(en, value.en) &&
                   Objects.equals(ua, value.ua);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(en, ua);
    }
}
