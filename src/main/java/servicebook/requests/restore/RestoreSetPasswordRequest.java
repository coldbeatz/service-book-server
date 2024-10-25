package servicebook.requests.restore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestoreSetPasswordRequest {

    private String key;
    private String password;
}
