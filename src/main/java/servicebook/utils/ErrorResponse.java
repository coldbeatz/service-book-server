package servicebook.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

    private String result = "error";
    private String code;

    public ErrorResponse(String code) {
        this.code = code;
    }

    public ErrorResponse() {
        this.code = "unknown_error";
    }
}
