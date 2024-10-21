package servicebook.exceptions;

import lombok.Getter;

@Getter
public class ClientException extends Exception {

    private final String code;

    public ClientException(String code, String message) {
        super(message);
        this.code = code;
    }
}
