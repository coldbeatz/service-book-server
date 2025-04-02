package servicebook.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegistrationRequest {

    private String email;
    private String password;

    private String fullName;
}
