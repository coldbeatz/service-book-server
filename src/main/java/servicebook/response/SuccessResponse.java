package servicebook.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessResponse {

    private String result;

    public SuccessResponse(String result) {
        this.result = result;
    }

    public SuccessResponse() {
        result = "success";
    }
}
