package servicebook.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserSettingsRequest {

    private String email;
    private String fullName;

    private boolean enableEmailNewsletter;

    private String currentPassword;
    private String newPassword;
}
