package servicebook.requests.login;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TokenValidationRequest {

    private String email;
    private String token;
}
