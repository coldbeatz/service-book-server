package servicebook.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import servicebook.localization.LocalizedString;

@Getter
@Setter
@ToString
public class LocalizedRequest {

    private String ua;
    private String en;

    public LocalizedString toLocalizedString() {
        LocalizedString localizedString = new LocalizedString();

        updateLocalizedString(localizedString);
        return localizedString;
    }

    public void updateLocalizedString(LocalizedString localizedString) {
        localizedString.setUa(ua);
        localizedString.setEn(en);
    }
}
