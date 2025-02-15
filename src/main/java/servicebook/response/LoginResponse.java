package servicebook.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String email;
    private String token;
}
