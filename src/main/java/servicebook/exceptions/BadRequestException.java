package servicebook.exceptions;

import lombok.Getter;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Кастомне виключення для помилки 400 Bad Request
 */
@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    private final String code;

    public BadRequestException(String code, String message) {
        super(message);
        this.code = code;
    }

    @SuppressWarnings("unused")
    public BadRequestException(String message) {
        super(message);
        this.code = "bad_request";
    }
}