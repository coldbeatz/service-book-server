package servicebook.exceptions;

import lombok.Getter;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Може використовуватися, коли користувач неавторизований або токен недійсний
 */
@Getter
@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

    private final String code;

    public UnauthorizedException(String code, String message) {
        super(message);
        this.code = code;
    }

    @SuppressWarnings("unused")
    public UnauthorizedException() {
        super("Unauthorized");
        this.code = "unauthorized";
    }
}